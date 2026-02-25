package com.inventory.repository;

import com.inventory.entity.StockReservation;
import com.inventory.entity.StockReservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    Optional<StockReservation> findByOrderIdAndProductId(String orderId, String productId);
    List<StockReservation> findByOrderId(String orderId);
    List<StockReservation> findByStatus(ReservationStatus status);
}
