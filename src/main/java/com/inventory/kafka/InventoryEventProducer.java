package com.inventory.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishInventoryReserved(String orderId, String productId, int qty) {
        String payload = String.format(
            "{\"orderId\":\"%s\",\"productId\":\"%s\",\"reservedQty\":%d,\"event\":\"INVENTORY_RESERVED\",\"timestamp\":\"%s\"}",
            orderId, productId, qty, LocalDateTime.now());
        kafkaTemplate.send("inventory.reserved", orderId, payload);
        log.info("[INVENTORY] Published inventory.reserved orderId={} productId={} qty={}", orderId, productId, qty);
    }

    public void publishInventoryUpdated(String productId, int newQty, String reason) {
        String payload = String.format(
            "{\"productId\":\"%s\",\"newQuantity\":%d,\"reason\":\"%s\",\"event\":\"INVENTORY_UPDATED\",\"timestamp\":\"%s\"}",
            productId, newQty, reason, LocalDateTime.now());
        kafkaTemplate.send("inventory.updated", productId, payload);
        log.info("[INVENTORY] Published inventory.updated productId={} newQty={}", productId, newQty);
    }

    public void publishLowStockAlert(String productId, String productName, int remainingQty) {
        String payload = String.format(
            "{\"productId\":\"%s\",\"productName\":\"%s\",\"remainingQty\":%d,\"event\":\"LOW_STOCK_ALERT\",\"timestamp\":\"%s\"}",
            productId, productName, remainingQty, LocalDateTime.now());
        kafkaTemplate.send("inventory.low.stock", productId, payload);
        log.warn("[INVENTORY] LOW STOCK ALERT! productId={} remaining={}", productId, remainingQty);
    }

    public void publishReservationReleased(String orderId, String productId, int qty) {
        String payload = String.format(
            "{\"orderId\":\"%s\",\"productId\":\"%s\",\"releasedQty\":%d,\"event\":\"RESERVATION_RELEASED\",\"timestamp\":\"%s\"}",
            orderId, productId, qty, LocalDateTime.now());
        kafkaTemplate.send("inventory.reservation.released", orderId, payload);
        log.info("[INVENTORY] Reservation released orderId={}", orderId);
    }
}
