package com.stackdarker.platform.auth.dto;

import java.util.Map;

public record ErrorItem(
        String code,
        String message,
        String field,
        Object rejectedValue,
        Map<String, Object> meta
) {}
