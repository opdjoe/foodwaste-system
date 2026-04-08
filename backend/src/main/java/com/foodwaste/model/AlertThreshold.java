package com.foodwaste.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "alert_threshold")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "min_threshold", nullable = false)
    private Double minThreshold;

    @Column(name = "max_threshold")
    private Double maxThreshold;

    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private InventoryItem inventoryItem;

    /**
     * Checks if the current quantity is below the minimum threshold.
     */
    public boolean checkThreshold(double currentQty) {
        return currentQty <= minThreshold;
    }
}
