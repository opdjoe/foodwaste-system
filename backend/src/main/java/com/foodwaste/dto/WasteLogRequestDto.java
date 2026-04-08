package com.foodwaste.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class WasteLogRequestDto {
    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be a positive number")
    private Double weight;

    @Size(max = 80, message = "Reason must not exceed 80 characters")
    private String reason;
}
