package com.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryRequest {
    @NotBlank  private String productId;
    @NotBlank  private String productName;
    @Min(0)    private int quantity;
    private int lowStockThreshold;
    private String warehouseLocation;
    private String sku;
}
