package com.stackdarker.platform.auth.api;

import com.stackdarker.platform.auth.api.dto.*;
import com.stackdarker.platform.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest req, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        authService.logout(userId, req);
        return ResponseEntity.noContent().build();
    }    


    private UUID extractUserId(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof UUID uuid) return uuid;

        try {
            return UUID.fromString(auth.getName());
        } catch (Exception ignored) {
            // last attempt: principal.toString()
            return UUID.fromString(String.valueOf(p));
        }
    }
}
