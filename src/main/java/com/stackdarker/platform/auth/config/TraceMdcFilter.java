package com.stackdarker.platform.auth.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Ensures trace/span IDs appear in logs (MDC) so Loki can extract them and link to Tempo.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE) // run late so the tracing span is already created
public class TraceMdcFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    public TraceMdcFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Span span = tracer.currentSpan();

        if (span != null) {
            String traceId = span.context().traceId();
            String spanId = span.context().spanId();

            // Put both styles so your existing patterns/regex work no matter what
            MDC.put("traceId", traceId);
            MDC.put("spanId", spanId);
            MDC.put("trace_id", traceId);
            MDC.put("span_id", spanId);

            // Optional: helps debugging from curl/Postman
            response.setHeader("X-Trace-Id", traceId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
            MDC.remove("spanId");
            MDC.remove("trace_id");
            MDC.remove("span_id");
        }
    }
}
