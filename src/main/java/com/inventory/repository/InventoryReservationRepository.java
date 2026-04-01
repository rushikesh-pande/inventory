package com.inventory.repository;
import com.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {
    List<InventoryReservation> findByOrderId(String orderId);
    List<InventoryReservation> findByStatus(InventoryReservation.ReservationStatus status);
}
