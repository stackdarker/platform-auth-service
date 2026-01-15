package com.stackdarker.platform.auth.api.error;

import java.util.Map;

public class ErrorItem {

    private String code;
    private String message;

    // field name for validation errors
    private String field;

    // space for any extra metadata (constraint, min/max, etc.)
    private Map<String, Object> meta;

    public ErrorItem() {}

    public ErrorItem(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorItem(String code, String message, String field, Map<String, Object> meta) {
        this.code = code;
        this.message = message;
        this.field = field;
        this.meta = meta;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public Map<String, Object> getMeta() { return meta; }
    public void setMeta(Map<String, Object> meta) { this.meta = meta; }
}
