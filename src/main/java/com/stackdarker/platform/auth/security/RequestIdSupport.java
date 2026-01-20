package com.stackdarker.platform.auth.security;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestIdSupport {
    private RequestIdSupport() {}

    public static String getRequestId(HttpServletRequest request) {
        String rid = request.getHeader("X-Request-Id");
        if (rid != null && !rid.isBlank()) return rid;

        Object attr = request.getAttribute("requestId");
        return attr == null ? null : attr.toString();
    }
}
