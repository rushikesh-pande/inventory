package com.inventory.service;
import com.inventory.dto.*;
import com.inventory.entity.*;
import com.inventory.exception.InventoryException;
import com.inventory.kafka.InventoryEventProducer;
import com.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor @Slf4j
public class InventoryService {
    private final InventoryItemRepository itemRepo;
    private final InventoryReservationRepository reservationRepo;
    private final InventoryEventProducer producer;

    @Transactional
    public CheckAndReserveResponse checkAndReserve(CheckAndReserveRequest req) {
        List<String> outOfStock = new ArrayList<>();
        for (CheckAndReserveRequest.Item item : req.getItems()) {
            InventoryItem inv = itemRepo.findByProductId(item.getProductId())
                .orElseThrow(() -> new InventoryException("Product not found: " + item.getProductId()));
            if (inv.getAvailableQty() < item.getRequiredQty()) {
                outOfStock.add(item.getProductId());
            }
        }
        if (!outOfStock.isEmpty()) {
            return CheckAndReserveResponse.builder().allAvailable(false).outOfStockProducts(outOfStock).build();
        }
        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        for (CheckAndReserveRequest.Item item : req.getItems()) {
            InventoryItem inv = itemRepo.findByProductId(item.getProductId()).get();
            inv.setAvailableQty(inv.getAvailableQty() - item.getRequiredQty());
            inv.setReservedQty(inv.getReservedQty() + item.getRequiredQty());
            inv.setUpdatedAt(LocalDateTime.now());
            itemRepo.save(inv);
            InventoryReservation res = InventoryReservation.builder()
                .reservationId(reservationId + "-" + item.getProductId())
                .productId(item.getProductId()).reservedQty(item.getRequiredQty())
                .status(InventoryReservation.ReservationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
            reservationRepo.save(res);
            if (inv.getAvailableQty() <= inv.getLowStockThreshold()) {
                producer.publishLowStock(inv);
            }
        }
        producer.publishReserved(reservationId, req.getItems());
        log.info("Reserved inventory reservationId={}", reservationId);
        return CheckAndReserveResponse.builder().allAvailable(true).reservationId(reservationId).build();
    }

    @Transactional
    public void updateStock(StockUpdateRequest req) {
        InventoryItem inv = itemRepo.findByProductId(req.getProductId())
            .orElseThrow(() -> new InventoryException("Product not found: " + req.getProductId()));
        inv.setAvailableQty(req.getNewQuantity());
        inv.setUpdatedAt(LocalDateTime.now());
        itemRepo.save(inv);
        producer.publishStockUpdated(inv);
        log.info("Stock updated productId={} qty={}", req.getProductId(), req.getNewQuantity());
    }

    public List<InventoryItem> getLowStockItems() {
        return itemRepo.findAll().stream()
            .filter(i -> i.getAvailableQty() <= i.getLowStockThreshold())
            .collect(Collectors.toList());
    }

    // Daily job — expire stale reservations
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void expireOldReservations() {
        reservationRepo.findByStatus(InventoryReservation.ReservationStatus.ACTIVE)
            .stream().filter(r -> r.getExpiresAt().isBefore(LocalDateTime.now()))
            .forEach(r -> { r.setStatus(InventoryReservation.ReservationStatus.EXPIRED); reservationRepo.save(r); });
        log.info("Expired stale inventory reservations");
    }
}
