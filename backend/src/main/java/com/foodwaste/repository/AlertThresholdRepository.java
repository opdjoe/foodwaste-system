package com.foodwaste.repository;

import com.foodwaste.model.AlertThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertThresholdRepository extends JpaRepository<AlertThreshold, Long> {

    Optional<AlertThreshold> findByInventoryItemId(Long itemId);

    boolean existsByInventoryItemId(Long itemId);
}
