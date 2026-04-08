package com.foodwaste.controller;

import com.foodwaste.dto.AlertThresholdRequestDto;
import com.foodwaste.dto.AlertThresholdResponseDto;
import com.foodwaste.dto.ApiResponseDto;
import com.foodwaste.service.AlertThresholdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertThresholdController {

    private final AlertThresholdService alertThresholdService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<AlertThresholdResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponseDto.ok(alertThresholdService.getAllThresholds()));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<ApiResponseDto<AlertThresholdResponseDto>> getByItemId(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponseDto.ok(alertThresholdService.getThresholdByItemId(itemId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<AlertThresholdResponseDto>> create(
            @Valid @RequestBody AlertThresholdRequestDto dto) {
        AlertThresholdResponseDto created = alertThresholdService.createThreshold(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Alert threshold created", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<AlertThresholdResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody AlertThresholdRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Alert updated",
                alertThresholdService.updateThreshold(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        alertThresholdService.deleteThreshold(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Alert threshold deleted", null));
    }
}
