package com.foodwaste.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Returned to the client after a successful login.
 *
 * NOTE: We avoid @AllArgsConstructor combined with a 'final' field because
 * Lombok cannot include final fields with initialisers in the generated
 * constructor — that causes a compile error / red underlines in STS4.
 * Instead we use @NoArgsConstructor and a plain setter-based builder approach,
 * or simply set fields in the constructor manually.
 */
@Data
@NoArgsConstructor
public class AuthResponseDto {
    private String token;
    private String username;
    private String role;
    private String message;

    public AuthResponseDto(String token, String username, String role) {
        this.token    = token;
        this.username = username;
        this.role     = role;
        this.message  = "Login successful";
    }
}
