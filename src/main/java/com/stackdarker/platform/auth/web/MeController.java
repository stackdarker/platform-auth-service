package com.stackdarker.platform.auth.web;

import com.stackdarker.platform.auth.api.dto.MeResponse;
import com.stackdarker.platform.auth.user.UserEntity;
import com.stackdarker.platform.auth.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class MeController {

    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        UUID userId;
        try {
            userId = UUID.fromString(auth.getName());
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        return userRepository.findById(userId)
                .map(u -> ResponseEntity.ok(
                        new MeResponse(u.getId(), u.getEmail(), u.getDisplayName())
                ))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
