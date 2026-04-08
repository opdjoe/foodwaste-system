package com.foodwaste.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnalyticsSummaryDto {
    private Double totalWasteAllTime;
    private Double totalWasteThisMonth;
    private Long totalLogsAllTime;
    private Long totalLogsThisMonth;
    private String topWastedItem;
    private List<WasteTrendDto> weeklyTrend;
    private List<ItemWasteSummaryDto> wasteByItem;
    private Long lowStockItemCount;
    private Long expiredItemCount;

    @Data
    @Builder
    public static class WasteTrendDto {
        private String period;
        private Double totalWaste;
        private Long logCount;
    }

    @Data
    @Builder
    public static class ItemWasteSummaryDto {
        private String itemName;
        private Double totalWaste;
        private String unit;
    }
}
