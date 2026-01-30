package com.stackdarker.platform.auth.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class RateLimitKeyResolver {

    public String resolve(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UUID userId) {
            return "user:" + userId;
        }

        // if behind a reverse proxy later, honor X-Forwarded-For
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String ip = xff.split(",")[0].trim();
            return "ip:" + ip;
        }

        String remoteAddr = Optional.ofNullable(request.getRemoteAddr()).orElse("unknown");
        return "ip:" + remoteAddr;
    }
}
