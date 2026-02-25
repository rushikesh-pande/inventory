package com.inventory.service;

import com.inventory.dto.*;
import com.inventory.entity.*;
import com.inventory.kafka.InventoryEventProducer;
import com.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final StockReservationRepository reservationRepo;
    private final InventoryEventProducer eventProducer;

    @Value("${inventory.low.stock.threshold:10}")
    private int defaultLowStockThreshold;

    // ── Create inventory item ─────────────────────────────────────────────────
    @Transactional
    public InventoryResponse addProduct(InventoryRequest req) {
        log.info("Adding product to inventory: {}", req.getProductId());
        InventoryItem item = InventoryItem.builder()
                .productId(req.getProductId())
                .productName(req.getProductName())
                .availableQuantity(req.getQuantity())
                .reservedQuantity(0)
                .lowStockThreshold(req.getLowStockThreshold() > 0 ? req.getLowStockThreshold() : defaultLowStockThreshold)
                .warehouseLocation(req.getWarehouseLocation())
                .sku(req.getSku())
                .build();
        item = inventoryRepo.save(item);
        eventProducer.publishInventoryUpdated(item.getProductId(), item.getAvailableQuantity(), "Initial stock");
        return toResponse(item);
    }

    // ── Check availability ────────────────────────────────────────────────────
    public boolean isAvailable(String productId, int quantity) {
        return inventoryRepo.findByProductId(productId)
                .map(i -> i.getAvailableQuantity() >= quantity)
                .orElse(false);
    }

    // ── Reserve stock for an order ────────────────────────────────────────────
    @Transactional
    public InventoryResponse reserveStock(ReserveRequest req) {
        log.info("Reserving {} units of {} for orderId={}", req.getQuantity(), req.getProductId(), req.getOrderId());
        InventoryItem item = inventoryRepo.findByProductId(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        if (item.getAvailableQuantity() < req.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + req.getProductId()
                + " — available: " + item.getAvailableQuantity() + ", requested: " + req.getQuantity());
        }

        item.setAvailableQuantity(item.getAvailableQuantity() - req.getQuantity());
        item.setReservedQuantity(item.getReservedQuantity() + req.getQuantity());
        item = inventoryRepo.save(item);

        reservationRepo.save(StockReservation.builder()
                .orderId(req.getOrderId())
                .productId(req.getProductId())
                .reservedQuantity(req.getQuantity())
                .status(StockReservation.ReservationStatus.ACTIVE)
                .build());

        eventProducer.publishInventoryReserved(req.getOrderId(), req.getProductId(), req.getQuantity());

        if (item.isLowStock()) {
            eventProducer.publishLowStockAlert(item.getProductId(), item.getProductName(), item.getAvailableQuantity());
        }
        return toResponse(item);
    }

    // ── Confirm reservation (order confirmed) ─────────────────────────────────
    @Transactional
    public void confirmReservation(String orderId) {
        reservationRepo.findByOrderId(orderId).forEach(r -> {
            r.setStatus(StockReservation.ReservationStatus.CONFIRMED);
            reservationRepo.save(r);
            log.info("Reservation confirmed for orderId={} productId={}", orderId, r.getProductId());
        });
    }

    // ── Release reservation (order cancelled) ─────────────────────────────────
    @Transactional
    public void releaseReservation(String orderId) {
        reservationRepo.findByOrderId(orderId).forEach(r -> {
            inventoryRepo.findByProductId(r.getProductId()).ifPresent(item -> {
                item.setAvailableQuantity(item.getAvailableQuantity() + r.getReservedQuantity());
                item.setReservedQuantity(Math.max(0, item.getReservedQuantity() - r.getReservedQuantity()));
                inventoryRepo.save(item);
            });
            r.setStatus(StockReservation.ReservationStatus.RELEASED);
            reservationRepo.save(r);
            eventProducer.publishReservationReleased(orderId, r.getProductId(), r.getReservedQuantity());
        });
    }

    // ── Deduct stock after payment ────────────────────────────────────────────
    @Transactional
    public void deductStockAfterPayment(String orderId) {
        reservationRepo.findByOrderId(orderId).forEach(r -> {
            inventoryRepo.findByProductId(r.getProductId()).ifPresent(item -> {
                item.setReservedQuantity(Math.max(0, item.getReservedQuantity() - r.getReservedQuantity()));
                inventoryRepo.save(item);
                eventProducer.publishInventoryUpdated(item.getProductId(), item.getAvailableQuantity(), "Post-payment deduction");
            });
            r.setStatus(StockReservation.ReservationStatus.CONFIRMED);
            reservationRepo.save(r);
        });
    }

    // ── Update stock ──────────────────────────────────────────────────────────
    @Transactional
    public InventoryResponse updateStock(StockUpdateRequest req) {
        InventoryItem item = inventoryRepo.findByProductId(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));
        item.setAvailableQuantity(Math.max(0, item.getAvailableQuantity() + req.getQuantityChange()));
        item = inventoryRepo.save(item);
        eventProducer.publishInventoryUpdated(item.getProductId(), item.getAvailableQuantity(), req.getReason());
        if (item.isLowStock()) {
            eventProducer.publishLowStockAlert(item.getProductId(), item.getProductName(), item.getAvailableQuantity());
        }
        return toResponse(item);
    }

    public InventoryResponse getInventory(String productId) {
        return inventoryRepo.findByProductId(productId).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
    }

    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepo.findByAvailableQuantityLessThanEqual(defaultLowStockThreshold)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private InventoryResponse toResponse(InventoryItem i) {
        return InventoryResponse.builder()
                .id(i.getId()).productId(i.getProductId()).productName(i.getProductName())
                .availableQuantity(i.getAvailableQuantity()).reservedQuantity(i.getReservedQuantity())
                .totalStock(i.getTotalStock()).lowStock(i.isLowStock())
                .warehouseLocation(i.getWarehouseLocation()).sku(i.getSku())
                .lastUpdated(i.getLastUpdated()).build();
    }
}
