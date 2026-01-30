package com.stackdarker.platform.auth.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.slf4j.MDC.get;

@Component
public class TraceResponseHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        // Micrometer tracing typically populates MDC keys traceId/spanId when log correlation is enabled.
        String traceId = get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            response.setHeader("X-Trace-Id", traceId);
        }
    }
}
