package com.stackdarker.platform.auth.api.error;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public class ErrorResponse {

    private Instant timestamp = Instant.now();
    private int status;
    private String error;       // HTTP reason phrase
    private String code;        // error code 
    private String message;     // readable summary
    private String path;        // request path
    private String requestId;   // X-Request-Id
    private List<ErrorItem> errors; 

    public ErrorResponse() {}

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public List<ErrorItem> getErrors() { return errors; }
    public void setErrors(List<ErrorItem> errors) { this.errors = errors; }

    public void setTimestamp(OffsetDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTimestamp'");
    }
}
