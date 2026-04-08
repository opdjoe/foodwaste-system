package com.foodwaste.service;

import com.foodwaste.dto.InventoryRequestDto;
import com.foodwaste.dto.InventoryResponseDto;
import com.foodwaste.exception.ResourceNotFoundException;
import com.foodwaste.model.AlertThreshold;
import com.foodwaste.model.InventoryItem;
import com.foodwaste.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Unit Tests")
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryItem sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = InventoryItem.builder()
                .id(1L)
                .name("Rice")
                .unit("kg")
                .currentQty(50.0)
                .expiryDate(LocalDate.now().plusDays(30))
                .build();
    }

    // ── getAllItems ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAllItems()")
    class GetAllItems {

        @Test
        @DisplayName("returns mapped DTOs for all stored items")
        void returnsAllItems() {
            when(inventoryRepository.findAll()).thenReturn(List.of(sampleItem));

            List<InventoryResponseDto> result = inventoryService.getAllItems();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Rice");
            assertThat(result.get(0).getCurrentQty()).isEqualTo(50.0);
        }

        @Test
        @DisplayName("returns empty list when no items exist")
        void returnsEmptyList() {
            when(inventoryRepository.findAll()).thenReturn(List.of());

            assertThat(inventoryService.getAllItems()).isEmpty();
        }
    }

    // ── getItemById ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getItemById()")
    class GetItemById {

        @Test
        @DisplayName("returns correct DTO when item exists")
        void returnsItemWhenFound() {
            when(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

            InventoryResponseDto dto = inventoryService.getItemById(1L);

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Rice");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when item does not exist")
        void throwsWhenNotFound() {
            when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.getItemById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ── createItem ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createItem()")
    class CreateItem {

        @Test
        @DisplayName("saves item and returns DTO with correct fields")
        void createsItemSuccessfully() {
            InventoryRequestDto dto = new InventoryRequestDto("Flour", "kg", 100.0, null);
            InventoryItem saved = InventoryItem.builder()
                    .id(2L).name("Flour").unit("kg").currentQty(100.0).build();

            when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(saved);

            InventoryResponseDto result = inventoryService.createItem(dto);

            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getName()).isEqualTo("Flour");
            verify(inventoryRepository).save(any(InventoryItem.class));
        }
    }

    // ── updateItem ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateItem()")
    class UpdateItem {

        @Test
        @DisplayName("updates fields and returns updated DTO")
        void updatesItemSuccessfully() {
            when(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
            when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(sampleItem);

            InventoryRequestDto dto = new InventoryRequestDto("Rice Updated", "kg", 80.0, null);
            InventoryResponseDto result = inventoryService.updateItem(1L, dto);

            assertThat(result.getName()).isEqualTo("Rice Updated");
            assertThat(result.getCurrentQty()).isEqualTo(80.0);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when item not found for update")
        void throwsWhenNotFound() {
            when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    inventoryService.updateItem(99L, new InventoryRequestDto("X", "kg", 1.0, null)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ── deleteItem ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteItem()")
    class DeleteItem {

        @Test
        @DisplayName("calls repository delete when item exists")
        void deletesSuccessfully() {
            when(inventoryRepository.existsById(1L)).thenReturn(true);

            inventoryService.deleteItem(1L);

            verify(inventoryRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when item does not exist")
        void throwsWhenNotFound() {
            when(inventoryRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> inventoryService.deleteItem(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ── lowStock mapping ─────────────────────────────────────────────────────

    @Test
    @DisplayName("toDto marks item as lowStock when quantity is at or below threshold")
    void marksLowStockCorrectly() {
        AlertThreshold threshold = AlertThreshold.builder()
                .id(1L)
                .minThreshold(60.0)   // item qty is 50 — below threshold
                .notificationEnabled(true)
                .inventoryItem(sampleItem)
                .build();
        sampleItem.setAlertThreshold(threshold);

        when(inventoryRepository.findAll()).thenReturn(List.of(sampleItem));

        List<InventoryResponseDto> result = inventoryService.getAllItems();

        assertThat(result.get(0).isLowStock()).isTrue();
    }

    @Test
    @DisplayName("toDto marks item as expired when expiryDate is in the past")
    void marksExpiredCorrectly() {
        sampleItem.setExpiryDate(LocalDate.now().minusDays(1));
        when(inventoryRepository.findAll()).thenReturn(List.of(sampleItem));

        List<InventoryResponseDto> result = inventoryService.getAllItems();

        assertThat(result.get(0).isExpired()).isTrue();
    }
}
