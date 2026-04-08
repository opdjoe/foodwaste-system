package com.foodwaste.service;

import com.foodwaste.dto.WasteLogRequestDto;
import com.foodwaste.dto.WasteLogResponseDto;
import com.foodwaste.exception.ResourceNotFoundException;
import com.foodwaste.model.InventoryItem;
import com.foodwaste.model.User;
import com.foodwaste.model.WasteLog;
import com.foodwaste.repository.UserRepository;
import com.foodwaste.repository.WasteLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WasteLogService {

    private final WasteLogRepository wasteLogRepository;
    private final InventoryService inventoryService;
    private final UserRepository userRepository;

    @Transactional
    public WasteLogResponseDto createLog(WasteLogRequestDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        InventoryItem item = inventoryService.findOrThrow(dto.getItemId());

        // Business logic: deduct waste from inventory
        item.reduceQuantity(dto.getWeight());

        WasteLog log = WasteLog.builder()
                .weight(dto.getWeight())
                .reason(dto.getReason())
                .timestamp(LocalDateTime.now())
                .inventoryItem(item)
                .user(user)
                .build();

        WasteLog saved = wasteLogRepository.save(log);
        this.log.info("WasteLog created: item='{}' weight={} by user='{}'",
                item.getName(), dto.getWeight(), username);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<WasteLogResponseDto> getAllLogs() {
        return wasteLogRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WasteLogResponseDto> getLogsByItem(Long itemId) {
        inventoryService.findOrThrow(itemId); // validate item exists
        return wasteLogRepository.findByInventoryItemIdOrderByTimestampDesc(itemId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WasteLogResponseDto> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return wasteLogRepository.findByDateRange(start, end)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WasteLogResponseDto getLogById(Long id) {
        WasteLog log = wasteLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteLog", id));
        return toDto(log);
    }

    private WasteLogResponseDto toDto(WasteLog log) {
        return WasteLogResponseDto.builder()
                .id(log.getId())
                .weight(log.getWeight())
                .reason(log.getReason())
                .timestamp(log.getTimestamp())
                .itemId(log.getInventoryItem().getId())
                .itemName(log.getInventoryItem().getName())
                .itemUnit(log.getInventoryItem().getUnit())
                .userId(log.getUser().getId())
                .loggedBy(log.getUser().getUsername())
                .build();
    }
}
