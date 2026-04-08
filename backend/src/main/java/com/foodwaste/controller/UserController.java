package com.foodwaste.controller;

import com.foodwaste.dto.ApiResponseDto;
import com.foodwaste.model.User;
import com.foodwaste.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponseDto.ok(userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok(userService.getUserById(id)));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponseDto<User>> updateRole(
            @PathVariable Long id,
            @RequestParam User.Role role) {
        return ResponseEntity.ok(ApiResponseDto.ok("Role updated", userService.updateRole(id, role)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponseDto.ok("User deleted", null));
    }
}
