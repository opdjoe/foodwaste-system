package com.foodwaste.repository;

import com.foodwaste.model.WasteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {

    List<WasteLog> findByUserIdOrderByTimestampDesc(Long userId);

    List<WasteLog> findByInventoryItemIdOrderByTimestampDesc(Long itemId);

    @Query("SELECT w FROM WasteLog w WHERE w.timestamp BETWEEN :start AND :end ORDER BY w.timestamp DESC")
    List<WasteLog> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT SUM(w.weight) FROM WasteLog w WHERE w.timestamp BETWEEN :start AND :end")
    Double sumWeightByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT SUM(w.weight) FROM WasteLog w")
    Double sumTotalWeight();

    @Query("SELECT COUNT(w) FROM WasteLog w WHERE w.timestamp BETWEEN :start AND :end")
    Long countByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
            SELECT w.inventoryItem.name, SUM(w.weight) as totalWaste
            FROM WasteLog w
            GROUP BY w.inventoryItem.name
            ORDER BY totalWaste DESC
            """)
    List<Object[]> findWasteGroupedByItem();

    @Query("""
            SELECT w.inventoryItem.name, SUM(w.weight) as totalWaste
            FROM WasteLog w
            WHERE w.timestamp BETWEEN :start AND :end
            GROUP BY w.inventoryItem.name
            ORDER BY totalWaste DESC
            """)
    List<Object[]> findWasteByItemInRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Weekly waste trend using a native MySQL query.
     *
     * Why native? Hibernate 6 (Spring Boot 3.x) validates JPQL WEEK() as a
     * 1-argument function. MySQL's WEEK(date, mode) takes 2 arguments — Hibernate
     * rejects it at startup with "Function week() has 1 parameters, but 2 arguments given".
     * Switching to nativeQuery = true sends the SQL directly to MySQL, bypassing
     * Hibernate's JPQL parser entirely.
     *
     * Result columns (by index):
     *   [0] week_num    INTEGER  - ISO week number (1-53)
     *   [1] year_num    INTEGER  - calendar year
     *   [2] total_weight DOUBLE  - sum of waste weight in kg
     *   [3] log_count   BIGINT   - number of waste log entries
     */
    @Query(value =
            "SELECT WEEK(w.timestamp, 1) AS week_num, " +
            "       YEAR(w.timestamp)    AS year_num, " +
            "       SUM(w.weight)        AS total_weight, " +
            "       COUNT(*)             AS log_count " +
            "FROM waste_logs w " +
            "WHERE w.timestamp >= :since " +
            "GROUP BY YEAR(w.timestamp), WEEK(w.timestamp, 1) " +
            "ORDER BY YEAR(w.timestamp), WEEK(w.timestamp, 1)",
            nativeQuery = true)
    List<Object[]> findWeeklyWasteTrend(@Param("since") LocalDateTime since);
}
