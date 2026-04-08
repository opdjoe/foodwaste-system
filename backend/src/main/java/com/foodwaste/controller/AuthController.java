package com.foodwaste.controller;

import com.foodwaste.dto.ApiResponseDto;
import com.foodwaste.dto.AuthResponseDto;
import com.foodwaste.dto.LoginRequestDto;
import com.foodwaste.dto.RegisterRequestDto;
import com.foodwaste.model.User;
import com.foodwaste.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> login(
            @Valid @RequestBody LoginRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        return ResponseEntity.ok(ApiResponseDto.ok("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<String>> register(
            @Valid @RequestBody RegisterRequestDto dto) {
        User user = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("User registered successfully",
                        "User ID: " + user.getId()));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponseDto<String>> health() {
        return ResponseEntity.ok(ApiResponseDto.ok("API is running", "OK"));
    }
}
