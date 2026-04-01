package com.inventory.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StockUpdateRequest {
    @NotBlank private String productId;
    @Min(0)   private int    newQuantity;
    private String reason;
}
