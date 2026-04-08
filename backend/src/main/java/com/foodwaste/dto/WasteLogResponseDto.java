package com.foodwaste.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WasteLogResponseDto {
    private Long id;
    private Double weight;
    private String reason;
    private LocalDateTime timestamp;
    private Long itemId;
    private String itemName;
    private String itemUnit;
    private Long userId;
    private String loggedBy;
}
