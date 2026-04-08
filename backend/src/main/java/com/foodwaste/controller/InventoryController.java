package com.foodwaste.controller;

import com.foodwaste.dto.ApiResponseDto;
import com.foodwaste.dto.InventoryRequestDto;
import com.foodwaste.dto.InventoryResponseDto;
import com.foodwaste.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<InventoryResponseDto>>> getAllItems() {
        return ResponseEntity.ok(ApiResponseDto.ok(inventoryService.getAllItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<InventoryResponseDto>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok(inventoryService.getItemById(id)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponseDto<List<InventoryResponseDto>>> getLowStockItems() {
        return ResponseEntity.ok(ApiResponseDto.ok(inventoryService.getLowStockItems()));
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponseDto<List<InventoryResponseDto>>> getExpiredItems() {
        return ResponseEntity.ok(ApiResponseDto.ok(inventoryService.getExpiredItems()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<InventoryResponseDto>> createItem(
            @Valid @RequestBody InventoryRequestDto dto) {
        InventoryResponseDto created = inventoryService.createItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Item created", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<InventoryResponseDto>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequestDto dto) {
        return ResponseEntity.ok(ApiResponseDto.ok("Item updated", inventoryService.updateItem(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Item deleted", null));
    }
}
