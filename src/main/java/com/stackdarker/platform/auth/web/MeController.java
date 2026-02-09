package com.stackdarker.platform.auth.web;

import com.stackdarker.platform.auth.api.dto.MeResponse;
import com.stackdarker.platform.auth.audit.AuthAuditEventType;
import com.stackdarker.platform.auth.audit.AuthAuditService;
import com.stackdarker.platform.auth.user.UserEntity;
import com.stackdarker.platform.auth.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Tag(name = "User", description = "Current user profile")
@SecurityRequirement(name = "bearer-jwt")
public class MeController {

    private final UserRepository userRepository;
    private final AuthAuditService audit;

    public MeController(UserRepository userRepository, AuthAuditService audit) {
        this.userRepository = userRepository;
        this.audit = audit;
    }

    @Operation(summary = "Get current user", description = "Returns the profile of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "User profile retrieved")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication, HttpServletRequest request) {

        UUID userId = extractUserId(authentication);
        if (userId == null) {
            audit.failure(AuthAuditEventType.ME, null, null, request, "missing_or_invalid_token");
            return ResponseEntity.status(401).build();
        }

        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            audit.failure(AuthAuditEventType.ME, userId, null, request, "user_not_found");
            return ResponseEntity.status(401).build();
        }

        UserEntity user = userOpt.get();
        audit.success(AuthAuditEventType.ME, user.getId(), user.getEmail(), request);

        return ResponseEntity.ok(
                new MeResponse(user.getId(), user.getEmail(), user.getDisplayName())
        );
    }

    private UUID extractUserId(Authentication auth) {
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UUID uuid) return uuid;

        try {
            return UUID.fromString(auth.getName());
        } catch (Exception ignored) {
            return null;
        }
    }
}
