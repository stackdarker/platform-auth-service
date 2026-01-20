package com.stackdarker.platform.auth.api;

import com.stackdarker.platform.auth.api.dto.MeResponse;
import com.stackdarker.platform.auth.user.UserEntity;
import com.stackdarker.platform.auth.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
public class MeController {

    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(); 

        return ResponseEntity.ok(new MeResponse(user.getId(), user.getEmail(), user.getDisplayName()));
    }
}
