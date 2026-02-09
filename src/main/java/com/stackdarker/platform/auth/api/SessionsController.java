package com.stackdarker.platform.auth.api;

import com.stackdarker.platform.auth.api.dto.SessionDto;
import com.stackdarker.platform.auth.token.RefreshTokenRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Tag(name = "Sessions", description = "Manage active user sessions")
@SecurityRequirement(name = "bearer-jwt")
public class SessionsController {

    private final RefreshTokenRepository refreshTokenRepository;

    public SessionsController(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Operation(summary = "List sessions", description = "Returns all sessions for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Sessions retrieved")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDto>> list(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();

        var sessions = refreshTokenRepository.findAllByUserId(userId).stream()
                .map(rt -> new SessionDto(rt.getToken(), rt.getExpiresAt(), rt.isRevoked()))
                .toList();

        return ResponseEntity.ok(sessions);
    }
}
