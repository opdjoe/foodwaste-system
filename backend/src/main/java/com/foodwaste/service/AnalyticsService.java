package com.foodwaste.service;

import com.foodwaste.dto.AnalyticsSummaryDto;
import com.foodwaste.dto.AnalyticsSummaryDto.ItemWasteSummaryDto;
import com.foodwaste.dto.AnalyticsSummaryDto.WasteTrendDto;
import com.foodwaste.repository.InventoryRepository;
import com.foodwaste.repository.WasteLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final WasteLogRepository wasteLogRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public AnalyticsSummaryDto getSummary() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now        = LocalDateTime.now();
        LocalDateTime eightWeeksAgo = now.minusWeeks(8);

        Double totalAll   = wasteLogRepository.sumTotalWeight();
        Double totalMonth = wasteLogRepository.sumWeightByDateRange(monthStart, now);
        Long   logsAll    = wasteLogRepository.count();
        Long   logsMonth  = wasteLogRepository.countByDateRange(monthStart, now);
        Long   lowStock   = inventoryRepository.countLowStockItems();
        Long   expired    = inventoryRepository.countExpiredItems(LocalDate.now());

        // Top wasted item all time
        List<Object[]> byItem = wasteLogRepository.findWasteGroupedByItem();
        String topItem = byItem.isEmpty() ? "N/A" : (String) byItem.get(0)[0];

        // Waste by item — top 10 in the last 8 weeks
        List<ItemWasteSummaryDto> wasteByItem = wasteLogRepository
                .findWasteByItemInRange(eightWeeksAgo, now)
                .stream()
                .limit(10)
                .map(row -> ItemWasteSummaryDto.builder()
                        .itemName((String) row[0])
                        .totalWaste(toDouble(row[1]))
                        .unit("kg")
                        .build())
                .collect(Collectors.toList());

        // Weekly trend — native query returns Number types (BigInteger / BigDecimal)
        // col[0] = week_num, col[1] = year_num, col[2] = total_weight, col[3] = log_count
        List<WasteTrendDto> weeklyTrend = wasteLogRepository
                .findWeeklyWasteTrend(eightWeeksAgo)
                .stream()
                .map(row -> WasteTrendDto.builder()
                        .period("W" + toInt(row[0]) + "-" + toInt(row[1]))
                        .totalWaste(toDouble(row[2]))
                        .logCount(toLong(row[3]))
                        .build())
                .collect(Collectors.toList());

        log.debug("Analytics summary generated: {} total logs, {} this month",
                logsAll, logsMonth);

        return AnalyticsSummaryDto.builder()
                .totalWasteAllTime(totalAll   != null ? totalAll   : 0.0)
                .totalWasteThisMonth(totalMonth != null ? totalMonth : 0.0)
                .totalLogsAllTime(logsAll)
                .totalLogsThisMonth(logsMonth)
                .topWastedItem(topItem)
                .weeklyTrend(weeklyTrend)
                .wasteByItem(wasteByItem)
                .lowStockItemCount(lowStock)
                .expiredItemCount(expired)
                .build();
    }

    // ── Safe numeric converters ───────────────────────────────────────────────
    // Native queries return java.math.BigInteger / BigDecimal — not int/long/double.
    // These helpers handle all Number subtypes safely.

    private static double toDouble(Object val) {
        if (val == null) return 0.0;
        return ((Number) val).doubleValue();
    }

    private static long toLong(Object val) {
        if (val == null) return 0L;
        return ((Number) val).longValue();
    }

    private static int toInt(Object val) {
        if (val == null) return 0;
        return ((Number) val).intValue();
    }
}
