package com.stackdarker.platform.auth.api.dto;

import java.time.Instant;
import java.util.UUID;

public class SessionDto {
    private UUID token;
    private Instant expiresAt;
    private boolean revoked;

    public SessionDto() {}

    public SessionDto(UUID token, Instant expiresAt, boolean revoked) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    public UUID getToken() { return token; }
    public void setToken(UUID token) { this.token = token; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}
