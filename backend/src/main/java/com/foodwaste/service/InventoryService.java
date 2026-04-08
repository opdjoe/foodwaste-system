package com.foodwaste.service;

import com.foodwaste.dto.InventoryRequestDto;
import com.foodwaste.dto.InventoryResponseDto;
import com.foodwaste.exception.ResourceNotFoundException;
import com.foodwaste.model.AlertThreshold;
import com.foodwaste.model.InventoryItem;
import com.foodwaste.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getAllItems() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryResponseDto getItemById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public InventoryResponseDto createItem(InventoryRequestDto dto) {
        InventoryItem item = InventoryItem.builder()
                .name(dto.getName())
                .unit(dto.getUnit())
                .currentQty(dto.getCurrentQty())
                .expiryDate(dto.getExpiryDate())
                .build();
        InventoryItem saved = inventoryRepository.save(item);
        log.info("Created inventory item: {} (id={})", saved.getName(), saved.getId());
        return toDto(saved);
    }

    @Transactional
    public InventoryResponseDto updateItem(Long id, InventoryRequestDto dto) {
        InventoryItem item = findOrThrow(id);
        item.setName(dto.getName());
        item.setUnit(dto.getUnit());
        item.setCurrentQty(dto.getCurrentQty());
        item.setExpiryDate(dto.getExpiryDate());
        log.info("Updated inventory item id={}", id);
        return toDto(inventoryRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("InventoryItem", id);
        }
        inventoryRepository.deleteById(id);
        log.info("Deleted inventory item id={}", id);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getLowStockItems() {
        return inventoryRepository.findLowStockItems()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getExpiredItems() {
        return inventoryRepository.findExpiredItems(LocalDate.now())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ── Internal helpers ────────────────────────────────────────────────────

    public InventoryItem findOrThrow(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", id));
    }

    InventoryResponseDto toDto(InventoryItem item) {
        AlertThreshold threshold = item.getAlertThreshold();
        boolean lowStock = threshold != null && threshold.checkThreshold(item.getCurrentQty());

        return InventoryResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .unit(item.getUnit())
                .currentQty(item.getCurrentQty())
                .expiryDate(item.getExpiryDate())
                .lowStock(lowStock)
                .expired(item.isExpired())
                .minThreshold(threshold != null ? threshold.getMinThreshold() : null)
                .build();
    }
}
