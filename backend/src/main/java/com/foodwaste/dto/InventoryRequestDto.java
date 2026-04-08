package com.foodwaste.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class InventoryRequestDto {
    @NotBlank(message = "Item name is required")
    private String name;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Double currentQty;

    private LocalDate expiryDate;
}
