package com.stackdarker.platform.auth.api.dto;

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
) {}