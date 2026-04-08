package com.foodwaste.controller;

import com.foodwaste.dto.ApiResponseDto;
import com.foodwaste.dto.WasteLogRequestDto;
import com.foodwaste.dto.WasteLogResponseDto;
import com.foodwaste.service.WasteLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/waste-logs")
@RequiredArgsConstructor
public class WasteLogController {

    private final WasteLogService wasteLogService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<WasteLogResponseDto>> createLog(
            @Valid @RequestBody WasteLogRequestDto dto) {
        WasteLogResponseDto created = wasteLogService.createLog(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Waste log created", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<WasteLogResponseDto>>> getAllLogs() {
        return ResponseEntity.ok(ApiResponseDto.ok(wasteLogService.getAllLogs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<WasteLogResponseDto>> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok(wasteLogService.getLogById(id)));
    }

    @GetMapping("/by-item/{itemId}")
    public ResponseEntity<ApiResponseDto<List<WasteLogResponseDto>>> getLogsByItem(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponseDto.ok(wasteLogService.getLogsByItem(itemId)));
    }

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponseDto<List<WasteLogResponseDto>>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(ApiResponseDto.ok(wasteLogService.getLogsByDateRange(start, end)));
    }
}
