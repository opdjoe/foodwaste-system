package com.foodwaste.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodwaste.dto.InventoryRequestDto;
import com.foodwaste.dto.InventoryResponseDto;
import com.foodwaste.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@DisplayName("InventoryController Integration Tests")
class InventoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private InventoryService inventoryService;

    private InventoryResponseDto sampleDto() {
        return InventoryResponseDto.builder()
                .id(1L).name("Rice").unit("kg")
                .currentQty(50.0).lowStock(false).expired(false).build();
    }

    // ── GET /api/inventory ──────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/inventory")
    class GetAll {

        @Test
        @WithMockUser(roles = "STAFF")
        @DisplayName("returns 200 with list of items for authenticated user")
        void returns200WithList() throws Exception {
            when(inventoryService.getAllItems()).thenReturn(List.of(sampleDto()));

            mockMvc.perform(get("/api/inventory"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data[0].name").value("Rice"))
                    .andExpect(jsonPath("$.data[0].currentQty").value(50.0));
        }

        @Test
        @DisplayName("returns 401 for unauthenticated requests")
        void returns401WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/inventory"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ── GET /api/inventory/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/inventory/{id}")
    class GetById {

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("returns 200 with item when found")
        void returnsItem() throws Exception {
            when(inventoryService.getItemById(1L)).thenReturn(sampleDto());

            mockMvc.perform(get("/api/inventory/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Rice"));
        }
    }

    // ── POST /api/inventory ─────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/inventory")
    class Create {

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("returns 201 when item created successfully")
        void createsItem() throws Exception {
            InventoryRequestDto req = new InventoryRequestDto("Flour", "kg", 100.0, null);
            when(inventoryService.createItem(any())).thenReturn(
                    InventoryResponseDto.builder().id(2L).name("Flour").unit("kg")
                            .currentQty(100.0).lowStock(false).expired(false).build());

            mockMvc.perform(post("/api/inventory")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("Flour"));
        }

        @Test
        @WithMockUser(roles = "STAFF")
        @DisplayName("returns 403 when STAFF role tries to create item")
        void returns403ForStaff() throws Exception {
            InventoryRequestDto req = new InventoryRequestDto("Flour", "kg", 100.0, null);

            mockMvc.perform(post("/api/inventory")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("returns 400 when request body is invalid")
        void returns400OnValidationFailure() throws Exception {
            // name is blank — should fail @NotBlank
            InventoryRequestDto req = new InventoryRequestDto("", "kg", -1.0, null);

            mockMvc.perform(post("/api/inventory")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ── PUT /api/inventory/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/inventory/{id}")
    class Update {

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("returns 200 when item updated")
        void updatesItem() throws Exception {
            InventoryRequestDto req = new InventoryRequestDto("Rice Updated", "kg", 80.0, null);
            InventoryResponseDto updated = InventoryResponseDto.builder()
                    .id(1L).name("Rice Updated").unit("kg")
                    .currentQty(80.0).lowStock(false).expired(false).build();

            when(inventoryService.updateItem(eq(1L), any())).thenReturn(updated);

            mockMvc.perform(put("/api/inventory/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("Rice Updated"));
        }
    }

    // ── DELETE /api/inventory/{id} ──────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/inventory/{id}")
    class Delete {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("returns 200 when item deleted by admin")
        void deletesItem() throws Exception {
            mockMvc.perform(delete("/api/inventory/1").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Item deleted"));
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("returns 403 when MANAGER tries to delete")
        void returns403ForManager() throws Exception {
            mockMvc.perform(delete("/api/inventory/1").with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}
