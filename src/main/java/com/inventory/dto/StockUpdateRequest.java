package com.inventory.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StockUpdateRequest {
    private String productId;
    private int quantityChange;  // positive = restock, negative = reduce
    private String reason;
}
