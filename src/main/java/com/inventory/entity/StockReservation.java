package com.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_reservations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String productId;

    private int reservedQuantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        reservedAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusMinutes(30); // 30-min hold
        }
    }

    public enum ReservationStatus {
        ACTIVE, CONFIRMED, RELEASED, EXPIRED
    }
}
