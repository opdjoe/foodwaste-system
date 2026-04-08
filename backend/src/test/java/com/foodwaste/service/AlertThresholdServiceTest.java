package com.foodwaste.service;

import com.foodwaste.dto.AlertThresholdRequestDto;
import com.foodwaste.dto.AlertThresholdResponseDto;
import com.foodwaste.exception.DuplicateResourceException;
import com.foodwaste.exception.ResourceNotFoundException;
import com.foodwaste.model.AlertThreshold;
import com.foodwaste.model.InventoryItem;
import com.foodwaste.repository.AlertThresholdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertThresholdService Unit Tests")
class AlertThresholdServiceTest {

    @Mock private AlertThresholdRepository alertThresholdRepository;
    @Mock private InventoryService inventoryService;

    @InjectMocks
    private AlertThresholdService alertThresholdService;

    private InventoryItem item;
    private AlertThreshold threshold;

    @BeforeEach
    void setUp() {
        item = InventoryItem.builder()
                .id(1L).name("Milk").unit("L").currentQty(30.0).build();

        threshold = AlertThreshold.builder()
                .id(1L).minThreshold(20.0).maxThreshold(200.0)
                .notificationEnabled(true).inventoryItem(item).build();

        item.setAlertThreshold(threshold);
    }

    @Nested
    @DisplayName("createThreshold()")
    class CreateThreshold {

        @Test
        @DisplayName("creates threshold when none exists for item")
        void createsSuccessfully() {
            AlertThresholdRequestDto dto =
                    new AlertThresholdRequestDto(1L, 20.0, 200.0, true);

            when(alertThresholdRepository.existsByInventoryItemId(1L)).thenReturn(false);
            when(inventoryService.findOrThrow(1L)).thenReturn(item);
            when(alertThresholdRepository.save(any())).thenReturn(threshold);

            AlertThresholdResponseDto result = alertThresholdService.createThreshold(dto);

            assertThat(result.getMinThreshold()).isEqualTo(20.0);
            assertThat(result.getItemName()).isEqualTo("Milk");
            assertThat(result.isCurrentlyTriggered()).isFalse(); // qty 30 > threshold 20
        }

        @Test
        @DisplayName("throws DuplicateResourceException when threshold already exists")
        void throwsWhenAlreadyExists() {
            when(alertThresholdRepository.existsByInventoryItemId(1L)).thenReturn(true);

            assertThatThrownBy(() ->
                    alertThresholdService.createThreshold(
                            new AlertThresholdRequestDto(1L, 20.0, null, true)))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("1");
        }
    }

    @Nested
    @DisplayName("updateThreshold()")
    class UpdateThreshold {

        @Test
        @DisplayName("updates min threshold correctly")
        void updatesSuccessfully() {
            when(alertThresholdRepository.findById(1L)).thenReturn(Optional.of(threshold));
            when(alertThresholdRepository.save(any())).thenReturn(threshold);

            AlertThresholdRequestDto dto =
                    new AlertThresholdRequestDto(1L, 35.0, 200.0, true);
            AlertThresholdResponseDto result = alertThresholdService.updateThreshold(1L, dto);

            assertThat(result.getMinThreshold()).isEqualTo(35.0);
            // qty=30 is now below new threshold of 35 → triggered
            assertThat(result.isCurrentlyTriggered()).isTrue();
        }

        @Test
        @DisplayName("throws when threshold id not found")
        void throwsWhenNotFound() {
            when(alertThresholdRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    alertThresholdService.updateThreshold(99L,
                            new AlertThresholdRequestDto(1L, 10.0, null, true)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteThreshold()")
    class DeleteThreshold {

        @Test
        @DisplayName("deletes when threshold exists")
        void deletesSuccessfully() {
            when(alertThresholdRepository.existsById(1L)).thenReturn(true);

            alertThresholdService.deleteThreshold(1L);

            verify(alertThresholdRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void throwsWhenNotFound() {
            when(alertThresholdRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> alertThresholdService.deleteThreshold(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllThresholds()")
    class GetAll {

        @Test
        @DisplayName("returns list of threshold DTOs")
        void returnsAll() {
            when(alertThresholdRepository.findAll()).thenReturn(List.of(threshold));

            List<AlertThresholdResponseDto> result = alertThresholdService.getAllThresholds();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getItemName()).isEqualTo("Milk");
        }
    }
}
