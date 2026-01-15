package com.stackdarker.platform.auth.security;

import com.stackdarker.platform.auth.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        if (props.getSecret() == null || props.getSecret().isBlank()) {
            throw new IllegalStateException("app.jwt.secret must be set");
        }
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(UserEntity user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getAccessTtlSeconds());

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(props.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("email", user.getEmail())
                .signWith(key)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(props.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID getUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }
}