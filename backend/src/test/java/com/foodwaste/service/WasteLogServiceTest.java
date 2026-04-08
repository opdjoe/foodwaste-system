package com.foodwaste.service;

import com.foodwaste.dto.WasteLogRequestDto;
import com.foodwaste.dto.WasteLogResponseDto;
import com.foodwaste.exception.ResourceNotFoundException;
import com.foodwaste.model.InventoryItem;
import com.foodwaste.model.User;
import com.foodwaste.model.WasteLog;
import com.foodwaste.repository.UserRepository;
import com.foodwaste.repository.WasteLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WasteLogService Unit Tests")
class WasteLogServiceTest {

    @Mock private WasteLogRepository wasteLogRepository;
    @Mock private InventoryService inventoryService;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private WasteLogService wasteLogService;

    private User testUser;
    private InventoryItem testItem;
    private WasteLog testLog;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).username("staff01").role(User.Role.STAFF).build();

        testItem = InventoryItem.builder()
                .id(1L).name("Chicken").unit("kg").currentQty(100.0).build();

        testLog = WasteLog.builder()
                .id(1L).weight(5.0).reason("Expired")
                .timestamp(LocalDateTime.now())
                .inventoryItem(testItem).user(testUser).build();

        // Set up security context mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("staff01");
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("createLog()")
    class CreateLog {

        @Test
        @DisplayName("creates log and reduces inventory quantity")
        void createsLogAndReducesInventory() {
            WasteLogRequestDto dto = new WasteLogRequestDto(1L, 5.0, "Expired");

            when(userRepository.findByUsername("staff01")).thenReturn(Optional.of(testUser));
            when(inventoryService.findOrThrow(1L)).thenReturn(testItem);
            when(wasteLogRepository.save(any(WasteLog.class))).thenReturn(testLog);

            WasteLogResponseDto result = wasteLogService.createLog(dto);

            assertThat(result.getWeight()).isEqualTo(5.0);
            assertThat(result.getItemName()).isEqualTo("Chicken");
            // Inventory should be reduced
            assertThat(testItem.getCurrentQty()).isEqualTo(95.0);
        }

        @Test
        @DisplayName("throws when inventory item not found")
        void throwsWhenItemNotFound() {
            WasteLogRequestDto dto = new WasteLogRequestDto(99L, 5.0, "Test");

            when(userRepository.findByUsername("staff01")).thenReturn(Optional.of(testUser));
            when(inventoryService.findOrThrow(99L))
                    .thenThrow(new ResourceNotFoundException("InventoryItem", 99L));

            assertThatThrownBy(() -> wasteLogService.createLog(dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("inventory quantity does not go below zero")
        void quantityDoesNotGoBelowZero() {
            testItem.setCurrentQty(3.0); // less than waste weight
            WasteLogRequestDto dto = new WasteLogRequestDto(1L, 10.0, "Over-waste");

            when(userRepository.findByUsername("staff01")).thenReturn(Optional.of(testUser));
            when(inventoryService.findOrThrow(1L)).thenReturn(testItem);
            when(wasteLogRepository.save(any(WasteLog.class))).thenReturn(testLog);

            wasteLogService.createLog(dto);

            assertThat(testItem.getCurrentQty()).isZero();
        }
    }

    @Nested
    @DisplayName("getAllLogs()")
    class GetAllLogs {

        @Test
        @DisplayName("returns all logs as DTOs")
        void returnsAllLogs() {
            when(wasteLogRepository.findAll()).thenReturn(List.of(testLog));

            List<WasteLogResponseDto> result = wasteLogService.getAllLogs();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getLoggedBy()).isEqualTo("staff01");
        }
    }

    @Nested
    @DisplayName("getLogById()")
    class GetLogById {

        @Test
        @DisplayName("returns correct log when found")
        void returnsLogWhenFound() {
            when(wasteLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

            WasteLogResponseDto result = wasteLogService.getLogById(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getReason()).isEqualTo("Expired");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when log does not exist")
        void throwsWhenNotFound() {
            when(wasteLogRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> wasteLogService.getLogById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }
}
