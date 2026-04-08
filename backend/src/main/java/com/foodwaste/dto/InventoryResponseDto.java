package com.foodwaste.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InventoryResponseDto {
    private Long id;
    private String name;
    private String unit;
    private Double currentQty;
    private LocalDate expiryDate;
    private boolean lowStock;
    private boolean expired;
    private Double minThreshold;
}
