package com.stackdarker.platform.auth.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties props;
    private final RateLimitKeyResolver keyResolver;
    private final Function<String, Bucket> loginBucket;
    private final Function<String, Bucket> registerBucket;
    private final Function<String, Bucket> refreshBucket;
    private final Function<String, Bucket> apiBucket;
    private final Function<String, Bucket> unauthApiBucket;

    public RateLimitingFilter(
            RateLimitProperties props,
            RateLimitKeyResolver keyResolver,
            Function<String, Bucket> loginBucket,
            Function<String, Bucket> registerBucket,
            Function<String, Bucket> refreshBucket,
            Function<String, Bucket> apiBucket,
            Function<String, Bucket> unauthApiBucket
    ) {
        this.props = props;
        this.keyResolver = keyResolver;
        this.loginBucket = loginBucket;
        this.registerBucket = registerBucket;
        this.refreshBucket = refreshBucket;
        this.apiBucket = apiBucket;
        this.unauthApiBucket = unauthApiBucket;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // never rate limit these
        if (path.equals("/v1/health")) return true;
        if (path.startsWith("/actuator")) return true;

        // only protect /v1 endpoints (adjust if needing to protect other paths)
        return !path.startsWith("/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (!props.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Use IP key for login/register (avoid account-lockout griefing by userId)
        String key = keyResolver.resolve(request);

        Bucket bucketToUse;

        if (HttpMethod.POST.matches(method) && path.equals("/v1/auth/login")) {
            bucketToUse = loginBucket.apply(key);
        } else if (HttpMethod.POST.matches(method) && path.equals("/v1/auth/register")) {
            bucketToUse = registerBucket.apply(key);
        } else if (HttpMethod.POST.matches(method) && path.equals("/v1/auth/refresh")) {
            bucketToUse = refreshBucket.apply(key);
        } else {
            // general API: prefer per-user when authenticated (keyResolver)
            // but if unauthenticated, still allow a bucket
            boolean authenticated = request.getHeader("Authorization") != null;
            bucketToUse = authenticated ? apiBucket.apply(key) : unauthApiBucket.apply(key);
        }

        ConsumptionProbe probe = bucketToUse.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            chain.doFilter(request, response);
            return;
        }

        long waitForNanos = probe.getNanosToWaitForRefill();
        long retryAfterSeconds = Math.max(1, Duration.ofNanos(waitForNanos).toSeconds());

        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setHeader("X-RateLimit-Remaining", "0");

        throw new RateLimitExceededException("Too many requests. Please retry later.");
    }
}
