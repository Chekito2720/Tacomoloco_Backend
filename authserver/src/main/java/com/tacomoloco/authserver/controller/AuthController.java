package com.tacomoloco.authserver.controller;

import com.tacomoloco.authserver.dto.request.LoginRequest;
import com.tacomoloco.authserver.dto.request.RegisterRequest;
import com.tacomoloco.authserver.dto.response.LoginResponse;
import com.tacomoloco.authserver.entity.User;
import com.tacomoloco.authserver.repository.UserRepository;
import com.tacomoloco.authserver.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByCorreo(request.getCorreo())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCorreo(request.getCorreo());
        user.setRol(request.getRol() != null ? request.getRol() : "CLIENTE");
        user.setActivo(true);

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByCorreo(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenService.generateToken(user.getCorreo(), "read write", user.getRol());
        LoginResponse response = new LoginResponse(token, "Bearer", 3600, "read write", user.getRol());
        return ResponseEntity.ok(response);
    }
}
