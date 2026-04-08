package com.foodwaste.controller;

import com.foodwaste.dto.AnalyticsSummaryDto;
import com.foodwaste.dto.ApiResponseDto;
import com.foodwaste.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponseDto<AnalyticsSummaryDto>> getSummary() {
        return ResponseEntity.ok(ApiResponseDto.ok(analyticsService.getSummary()));
    }
}
