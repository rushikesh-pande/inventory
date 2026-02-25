package com.inventory.kafka;

import com.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order.created", groupId = "inventory-group")
    public void onOrderCreated(ConsumerRecord<String, String> record) {
        log.info("[INVENTORY] Received order.created — confirming reservation for orderId={}", record.key());
        inventoryService.confirmReservation(record.key());
    }

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-group")
    public void onOrderCancelled(ConsumerRecord<String, String> record) {
        log.info("[INVENTORY] Received order.cancelled — releasing reservation for orderId={}", record.key());
        inventoryService.releaseReservation(record.key());
    }

    @KafkaListener(topics = "payment.completed", groupId = "inventory-group")
    public void onPaymentCompleted(ConsumerRecord<String, String> record) {
        log.info("[INVENTORY] Received payment.completed — deducting stock for orderId={}", record.key());
        inventoryService.deductStockAfterPayment(record.key());
    }
}
