package com.stackdarker.platform.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class RefreshTokenCodec {
    private static final SecureRandom RNG = new SecureRandom();

    private RefreshTokenCodec() {}

    /** Opaque token presented to clients (Base64URL). */
    public static String newToken() {
        byte[] bytes = new byte[32]; // 256-bit
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Hex SHA-256 of the presented token. Stored in DB. */
    public static String sha256Hex(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash refresh token", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
