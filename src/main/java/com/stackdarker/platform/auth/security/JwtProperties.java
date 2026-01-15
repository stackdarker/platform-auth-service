package com.stackdarker.platform.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * In dev, hardcode in application.yml.
     * In real env, inject via env var / secret manager.
     */
    private String secret;

    private String issuer = "platform-auth-service";

    /**
     * Access token lifetime in seconds (900 = 15 minutes)
     */
    private long accessTtlSeconds = 900;

    /**
     * Refresh token lifetime in seconds (1209600 = 14 days)
     */
    private long refreshTtlSeconds = 1209600;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public long getAccessTtlSeconds() { return accessTtlSeconds; }
    public void setAccessTtlSeconds(long accessTtlSeconds) { this.accessTtlSeconds = accessTtlSeconds; }

    public long getRefreshTtlSeconds() { return refreshTtlSeconds; }
    public void setRefreshTtlSeconds(long refreshTtlSeconds) { this.refreshTtlSeconds = refreshTtlSeconds; }
}