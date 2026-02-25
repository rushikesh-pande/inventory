package com.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String productId;

    @NotBlank
    private String productName;

    @Min(0)
    private int availableQuantity;

    @Min(0)
    private int reservedQuantity;

    private int lowStockThreshold;
    private String warehouseLocation;
    private String sku;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    public int getTotalStock() {
        return availableQuantity + reservedQuantity;
    }

    public boolean isLowStock() {
        return availableQuantity <= lowStockThreshold;
    }
}
