package com.stackdarker.platform.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    List<RefreshTokenEntity> findAllByUserId(UUID userId);

    Optional<RefreshTokenEntity> findByToken(UUID token);

    long deleteByExpiresAtBefore(Instant now);

    List<RefreshTokenEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}
