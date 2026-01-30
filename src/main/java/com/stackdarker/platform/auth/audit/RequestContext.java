package com.stackdarker.platform.auth.audit;

public record RequestContext(
        String requestId,
        String traceId,
        String ip,
        String userAgent,
        String httpMethod,
        String httpPath
) {}
