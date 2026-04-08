package com.foodwaste.repository;

import com.foodwaste.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByNameContainingIgnoreCase(String name);

    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate < :today")
    List<InventoryItem> findExpiredItems(LocalDate today);

    @Query("SELECT i FROM InventoryItem i JOIN i.alertThreshold a WHERE i.currentQty <= a.minThreshold AND a.notificationEnabled = true")
    List<InventoryItem> findLowStockItems();

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.expiryDate < :today")
    Long countExpiredItems(LocalDate today);

    @Query("SELECT COUNT(i) FROM InventoryItem i JOIN i.alertThreshold a WHERE i.currentQty <= a.minThreshold")
    Long countLowStockItems();
}
