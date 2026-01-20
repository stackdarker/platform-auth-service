package com.stackdarker.platform.auth.api;

import com.stackdarker.platform.auth.api.dto.SessionDto;
import com.stackdarker.platform.auth.token.RefreshTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class SessionsController {

    private final RefreshTokenRepository refreshTokenRepository;

    public SessionsController(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDto>> list(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();

        var sessions = refreshTokenRepository.findAllByUserId(userId).stream()
                .map(rt -> new SessionDto(rt.getToken(), rt.getExpiresAt(), rt.isRevoked()))
                .toList();

        return ResponseEntity.ok(sessions);
    }
}
