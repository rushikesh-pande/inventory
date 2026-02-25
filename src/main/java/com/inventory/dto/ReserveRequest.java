package com.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReserveRequest {
    @NotBlank private String orderId;
    @NotBlank private String productId;
    @Min(1)   private int quantity;
}
