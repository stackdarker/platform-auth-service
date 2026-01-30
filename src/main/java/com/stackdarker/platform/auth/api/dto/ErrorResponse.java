package com.stackdarker.platform.auth.api.dto;


import com.stackdarker.platform.auth.ratelimit.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        String requestId,
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String code,
        List<ErrorItem> details
) {

@ExceptionHandler(RateLimitExceededException.class)
public ResponseEntity<?> handleRateLimit(RateLimitExceededException ex, HttpServletRequest req) {
    Map<String, Object> body = Map.of(
            "timestamp", Instant.now().toString(),
            "status", 429,
            "error", "Too Many Requests",
            "code", "RATE_LIMITED",
            "message", ex.getMessage(),
            "path", req.getRequestURI(),
            "requestId", req.getHeader("X-Request-Id"),
            "errors", new Object[] { Map.of("code", "RATE_LIMITED", "message", ex.getMessage(), "field", null, "meta", null) }
    );
    return ResponseEntity.status(429).body(body);
}
}