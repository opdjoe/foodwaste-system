package com.foodwaste.service;

import com.foodwaste.config.JwtUtils;
import com.foodwaste.dto.AuthResponseDto;
import com.foodwaste.dto.LoginRequestDto;
import com.foodwaste.dto.RegisterRequestDto;
import com.foodwaste.exception.DuplicateResourceException;
import com.foodwaste.model.User;
import com.foodwaste.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public User register(RegisterRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + dto.getUsername());
        }
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + dto.getEmail());
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();

        User saved = userRepository.save(user);
        log.info("Registered new user: {} with role: {}", saved.getUsername(), saved.getRole());
        return saved;
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);

        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow();
        log.info("User logged in: {}", dto.getUsername());
        return new AuthResponseDto(token, user.getUsername(), user.getRole().name());
    }
}
