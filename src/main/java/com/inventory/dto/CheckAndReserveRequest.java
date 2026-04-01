package com.inventory.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CheckAndReserveRequest {
    @NotEmpty private List<Item> items;
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Item {
        @NotBlank private String productId;
        @Min(1)   private int    requiredQty;
    }
}
