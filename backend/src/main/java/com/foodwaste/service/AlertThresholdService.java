package com.foodwaste.service;

import com.foodwaste.dto.AlertThresholdRequestDto;
import com.foodwaste.dto.AlertThresholdResponseDto;
import com.foodwaste.exception.DuplicateResourceException;
import com.foodwaste.exception.ResourceNotFoundException;
import com.foodwaste.model.AlertThreshold;
import com.foodwaste.model.InventoryItem;
import com.foodwaste.repository.AlertThresholdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertThresholdService {

    private final AlertThresholdRepository alertThresholdRepository;
    private final InventoryService inventoryService;

    @Transactional
    public AlertThresholdResponseDto createThreshold(AlertThresholdRequestDto dto) {
        if (alertThresholdRepository.existsByInventoryItemId(dto.getItemId())) {
            throw new DuplicateResourceException(
                    "Alert threshold already exists for item id: " + dto.getItemId());
        }
        InventoryItem item = inventoryService.findOrThrow(dto.getItemId());

        AlertThreshold threshold = AlertThreshold.builder()
                .inventoryItem(item)
                .minThreshold(dto.getMinThreshold())
                .maxThreshold(dto.getMaxThreshold())
                .notificationEnabled(dto.getNotificationEnabled() != null
                        ? dto.getNotificationEnabled() : true)
                .build();

        AlertThreshold saved = alertThresholdRepository.save(threshold);
        log.info("Alert threshold created for item '{}': min={}", item.getName(), dto.getMinThreshold());
        return toDto(saved);
    }

    @Transactional
    public AlertThresholdResponseDto updateThreshold(Long id, AlertThresholdRequestDto dto) {
        AlertThreshold threshold = alertThresholdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertThreshold", id));

        threshold.setMinThreshold(dto.getMinThreshold());
        threshold.setMaxThreshold(dto.getMaxThreshold());
        if (dto.getNotificationEnabled() != null) {
            threshold.setNotificationEnabled(dto.getNotificationEnabled());
        }

        log.info("Updated alert threshold id={}", id);
        return toDto(alertThresholdRepository.save(threshold));
    }

    @Transactional(readOnly = true)
    public List<AlertThresholdResponseDto> getAllThresholds() {
        return alertThresholdRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlertThresholdResponseDto getThresholdByItemId(Long itemId) {
        AlertThreshold threshold = alertThresholdRepository.findByInventoryItemId(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No alert threshold for item id: " + itemId));
        return toDto(threshold);
    }

    @Transactional
    public void deleteThreshold(Long id) {
        if (!alertThresholdRepository.existsById(id)) {
            throw new ResourceNotFoundException("AlertThreshold", id);
        }
        alertThresholdRepository.deleteById(id);
        log.info("Deleted alert threshold id={}", id);
    }

    private AlertThresholdResponseDto toDto(AlertThreshold t) {
        InventoryItem item = t.getInventoryItem();
        return AlertThresholdResponseDto.builder()
                .id(t.getId())
                .itemId(item.getId())
                .itemName(item.getName())
                .minThreshold(t.getMinThreshold())
                .maxThreshold(t.getMaxThreshold())
                .notificationEnabled(t.getNotificationEnabled())
                .currentlyTriggered(t.checkThreshold(item.getCurrentQty()))
                .build();
    }
}
