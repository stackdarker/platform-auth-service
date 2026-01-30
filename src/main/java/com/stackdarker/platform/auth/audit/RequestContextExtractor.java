package com.stackdarker.platform.auth.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class RequestContextExtractor {

    public RequestContext from(HttpServletRequest req) {
        String requestId = firstNonBlank(
                req.getHeader("X-Request-Id"),
                MDC.get("requestId")
        );

        String traceId = firstNonBlank(
                req.getHeader("X-Trace-Id"),
                MDC.get("traceId")
        );

        String ip = extractClientIp(req);
        String userAgent = req.getHeader("User-Agent");

        return new RequestContext(
                requestId,
                traceId,
                ip,
                userAgent,
                req.getMethod(),
                req.getRequestURI()
        );
    }

    private String extractClientIp(HttpServletRequest req) {
        // if you ever put a reverse proxy in front, this becomes useful
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // first IP in list is the client
            return xff.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
}
