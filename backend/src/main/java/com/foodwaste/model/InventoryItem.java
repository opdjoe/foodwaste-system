package com.foodwaste.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotBlank
    @Column(name = "unit", length = 50, nullable = false)
    private String unit;

    @NotNull
    @PositiveOrZero
    @Column(name = "current_qty", nullable = false)
    private Double currentQty;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @OneToOne(mappedBy = "inventoryItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AlertThreshold alertThreshold;

    /**
     * Reduces inventory quantity by the given amount.
     * Quantity will not go below zero.
     */
    public void reduceQuantity(double amount) {
        this.currentQty = Math.max(0, this.currentQty - amount);
    }

    /**
     * Checks if the item has expired.
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}
