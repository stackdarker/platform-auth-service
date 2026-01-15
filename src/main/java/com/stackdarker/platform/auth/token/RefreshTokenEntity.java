package com.stackdarker.platform.auth.token;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "token", nullable = false, unique = true, updatable = false)
    private UUID token;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    public RefreshTokenEntity() {}

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (token == null) token = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getToken() { return token; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}