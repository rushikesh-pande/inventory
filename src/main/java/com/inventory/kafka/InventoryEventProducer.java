package com.inventory.kafka;
import com.inventory.dto.CheckAndReserveRequest;
import com.inventory.entity.InventoryItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.*;
@Component @RequiredArgsConstructor @Slf4j
public class InventoryEventProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    public void publishReserved(String reservationId, List<CheckAndReserveRequest.Item> items) {
        Map<String,Object> e = new HashMap<>();
        e.put("eventType","INVENTORY_RESERVED"); e.put("reservationId",reservationId);
        e.put("items",items); e.put("timestamp",System.currentTimeMillis());
        kafkaTemplate.send("inventory.reserved", reservationId, e);
        log.info("Published INVENTORY_RESERVED reservationId={}", reservationId);
    }
    public void publishStockUpdated(InventoryItem item) {
        Map<String,Object> e = new HashMap<>();
        e.put("eventType","INVENTORY_UPDATED"); e.put("productId",item.getProductId());
        e.put("availableQty",item.getAvailableQty()); e.put("timestamp",System.currentTimeMillis());
        kafkaTemplate.send("inventory.updated", item.getProductId(), e);
        log.info("Published INVENTORY_UPDATED productId={}", item.getProductId());
    }
    public void publishLowStock(InventoryItem item) {
        Map<String,Object> e = new HashMap<>();
        e.put("eventType","LOW_STOCK_ALERT"); e.put("productId",item.getProductId());
        e.put("availableQty",item.getAvailableQty()); e.put("threshold",item.getLowStockThreshold());
        e.put("timestamp",System.currentTimeMillis());
        kafkaTemplate.send("inventory.low.stock", item.getProductId(), e);
        log.warn("Published LOW_STOCK_ALERT productId={} qty={}", item.getProductId(), item.getAvailableQty());
    }
}
