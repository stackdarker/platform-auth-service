package com.stackdarker.platform.auth.api;

import com.stackdarker.platform.auth.api.dto.MeResponse;
import com.stackdarker.platform.auth.user.UserEntity;
import com.stackdarker.platform.auth.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
public class MeController {

    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public MeResponse me(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        UserEntity user = userRepository.findById(userId).orElseThrow();

        return new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
