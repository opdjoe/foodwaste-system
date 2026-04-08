package com.foodwaste.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertThresholdResponseDto {
    private Long id;
    private Long itemId;
    private String itemName;
    private Double minThreshold;
    private Double maxThreshold;
    private Boolean notificationEnabled;
    private boolean currentlyTriggered;
}
