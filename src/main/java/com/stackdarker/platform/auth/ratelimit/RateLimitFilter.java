package com.stackdarker.platform.auth.ratelimit;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;

    public RateLimitFilter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();

        Bandwidth limit;

        if (path.startsWith("/v1/auth/login")) {
            limit = Bandwidth.simple(5, Duration.ofMinutes(1));
        } else if (path.startsWith("/v1/auth/register")) {
            limit = Bandwidth.simple(3, Duration.ofMinutes(1));
        } else if (path.startsWith("/v1/auth/refresh")) {
            limit = Bandwidth.simple(10, Duration.ofMinutes(1));
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        String key = "rl:" + path + ":" + ip;

        Bucket bucket = proxyManager.builder()
                .build(key, () -> BucketConfiguration.builder()
                        .addLimit(limit)
                        .build());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }
    }
}
