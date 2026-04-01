package com.inventory.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="inventory_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true) private String productId;
    private String productName;
    private int availableQty;
    private int reservedQty;
    private int lowStockThreshold;
    private LocalDateTime updatedAt;
}
