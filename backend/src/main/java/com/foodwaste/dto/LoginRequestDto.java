package com.foodwaste.dto;

import com.foodwaste.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

// =============================================
//  AUTH
// =============================================
@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
}
