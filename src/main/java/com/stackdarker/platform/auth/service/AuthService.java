package com.stackdarker.platform.auth.service;

import com.stackdarker.platform.auth.api.dto.AuthResponse;
import com.stackdarker.platform.auth.api.dto.LoginRequest;
import com.stackdarker.platform.auth.api.dto.RefreshRequest;
import com.stackdarker.platform.auth.api.dto.RegisterRequest;
import com.stackdarker.platform.auth.security.JwtProperties;
import com.stackdarker.platform.auth.security.JwtService;
import com.stackdarker.platform.auth.service.exceptions.EmailAlreadyExistsException;
import com.stackdarker.platform.auth.service.exceptions.InvalidCredentialsException;
import com.stackdarker.platform.auth.token.RefreshTokenEntity;
import com.stackdarker.platform.auth.token.RefreshTokenRepository;
import com.stackdarker.platform.auth.user.UserEntity;
import com.stackdarker.platform.auth.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtProperties jwtProperties
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        final String emailNormalized = requireEmail(normalizeEmail(request.getEmail()));

        if (userRepository.existsByEmailIgnoreCase(emailNormalized)) {
            throw new EmailAlreadyExistsException(emailNormalized);
        }

        UserEntity user = new UserEntity();
        user.setEmail(emailNormalized);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(null);

        UserEntity saved = userRepository.save(user);
        return issueTokens(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        final String emailNormalized = requireEmail(normalizeEmail(request.getEmail()));

        UserEntity user = userRepository.findByEmailIgnoreCase(emailNormalized)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        UUID token = parseRefreshToken(request.getRefreshToken());

        RefreshTokenEntity rt = refreshTokenRepository.findByToken(token)
                .orElseThrow(InvalidCredentialsException::new);

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidCredentialsException();
        }

        UserEntity user = userRepository.findById(rt.getUserId())
                .orElseThrow(InvalidCredentialsException::new);

        // rotate refresh token
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        return issueTokens(user);
    }

    private AuthResponse issueTokens(UserEntity user) {
        String access = jwtService.createAccessToken(user);

        RefreshTokenEntity rt = new RefreshTokenEntity();
        rt.setUserId(user.getId());
        rt.setExpiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTtlSeconds()));

        RefreshTokenEntity saved = refreshTokenRepository.save(rt);

        return new AuthResponse(
                access,
                saved.getToken().toString(),
                jwtProperties.getAccessTtlSeconds()
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String requireEmail(String normalizedEmail) {
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new InvalidCredentialsException();
        }
        return normalizedEmail;
    }

    private UUID parseRefreshToken(String refreshToken) {
        try {
            return UUID.fromString(refreshToken);
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }
}
