package com.inventory.repository;

import com.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByProductId(String productId);
    List<InventoryItem> findByAvailableQuantityLessThanEqual(int threshold);
}
