package com.inventory.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="inventory_reservations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryReservation {
    @Id private String reservationId;
    private String orderId;
    private String productId;
    private int    reservedQty;
    @Enumerated(EnumType.STRING) private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    public enum ReservationStatus { ACTIVE, CONFIRMED, RELEASED, EXPIRED }
}
