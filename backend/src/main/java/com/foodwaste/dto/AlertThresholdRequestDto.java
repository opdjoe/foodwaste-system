package com.foodwaste.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AlertThresholdRequestDto {
    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Minimum threshold is required")
    @PositiveOrZero(message = "Minimum threshold must be zero or positive")
    private Double minThreshold;

    private Double maxThreshold;

    private Boolean notificationEnabled = true;
}
