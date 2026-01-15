package com.stackdarker.platform.auth.service;

import com.stackdarker.platform.auth.api.dto.LoginRequest;
import com.stackdarker.platform.auth.api.dto.MeResponse;
import com.stackdarker.platform.auth.api.dto.RegisterRequest;
import com.stackdarker.platform.auth.service.exceptions.EmailAlreadyExistsException;
import com.stackdarker.platform.auth.service.exceptions.InvalidCredentialsException;
import com.stackdarker.platform.auth.user.UserEntity;
import com.stackdarker.platform.auth.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MeResponse register(RegisterRequest request) {
        final String emailNormalized = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(emailNormalized)) {
            throw new EmailAlreadyExistsException(emailNormalized);
        }

        UserEntity user = new UserEntity();
        user.setEmail(emailNormalized);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // displayName is optional; set later via profile endpoint
        user.setDisplayName(null);

        UserEntity saved = userRepository.save(user);
        return new MeResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getDisplayName(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public MeResponse login(LoginRequest request) {
        final String emailNormalized = normalizeEmail(request.getEmail());

        UserEntity user = userRepository.findByEmailIgnoreCase(emailNormalized)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // No JWT yet.
        // For now, just return user info to prove credentials flow works.
        // Next step will change this to AuthResponse (access/refresh tokens).
        return new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
