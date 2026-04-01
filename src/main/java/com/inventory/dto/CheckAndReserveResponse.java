package com.inventory.dto;
import lombok.*;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CheckAndReserveResponse {
    private boolean allAvailable;
    private String  reservationId;
    private List<String> outOfStockProducts;
}
