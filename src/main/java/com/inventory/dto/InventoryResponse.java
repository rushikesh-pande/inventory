package com.inventory.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryResponse {
    private Long id;
    private String productId;
    private String productName;
    private int availableQuantity;
    private int reservedQuantity;
    private int totalStock;
    private boolean lowStock;
    private String warehouseLocation;
    private String sku;
    private LocalDateTime lastUpdated;
}
