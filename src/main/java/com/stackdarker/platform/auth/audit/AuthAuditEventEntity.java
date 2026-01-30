package com.stackdarker.platform.auth.audit;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_audit_events")
public class AuthAuditEventEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private AuthAuditEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false)
    private AuthAuditOutcome outcome;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email")
    private String email;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "ip")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "http_path")
    private String httpPath;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected AuthAuditEventEntity() {}

    public AuthAuditEventEntity(
            AuthAuditEventType eventType,
            AuthAuditOutcome outcome,
            UUID userId,
            String email,
            String requestId,
            String traceId,
            String ip,
            String userAgent,
            String httpMethod,
            String httpPath,
            String failureReason
    ) {
        this.eventType = eventType;
        this.outcome = outcome;
        this.userId = userId;
        this.email = email;
        this.requestId = requestId;
        this.traceId = traceId;
        this.ip = ip;
        this.userAgent = userAgent;
        this.httpMethod = httpMethod;
        this.httpPath = httpPath;
        this.failureReason = failureReason;
    }

    // getters (only what is needed; IDE can generate)
    public UUID getId() { return id; }
    public AuthAuditEventType getEventType() { return eventType; }
    public AuthAuditOutcome getOutcome() { return outcome; }
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRequestId() { return requestId; }
    public String getTraceId() { return traceId; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }
    public String getHttpMethod() { return httpMethod; }
    public String getHttpPath() { return httpPath; }
    public String getFailureReason() { return failureReason; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}