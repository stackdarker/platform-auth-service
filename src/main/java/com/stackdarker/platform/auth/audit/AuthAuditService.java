package com.stackdarker.platform.auth.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthAuditService {

    private static final Logger log = LoggerFactory.getLogger("AUTH_AUDIT");

    private final AuthAuditEventRepository repo;
    private final RequestContextExtractor ctxExtractor;

    public AuthAuditService(AuthAuditEventRepository repo, RequestContextExtractor ctxExtractor) {
        this.repo = repo;
        this.ctxExtractor = ctxExtractor;
    }

    @Transactional
    public void success(AuthAuditEventType type, UUID userId, String email, HttpServletRequest req) {
        write(type, AuthAuditOutcome.SUCCESS, userId, email, req, null);
    }

    @Transactional
    public void failure(AuthAuditEventType type, UUID userId, String email, HttpServletRequest req, String reason) {
        write(type, AuthAuditOutcome.FAILURE, userId, email, req, reason);
    }

    private void write(
            AuthAuditEventType type,
            AuthAuditOutcome outcome,
            UUID userId,
            String email,
            HttpServletRequest req,
            String reason
    ) {
        RequestContext ctx = ctxExtractor.from(req);

        AuthAuditEventEntity event = new AuthAuditEventEntity(
                type,
                outcome,
                userId,
                email,
                ctx.requestId(),
                ctx.traceId(),
                ctx.ip(),
                ctx.userAgent(),
                ctx.httpMethod(),
                ctx.httpPath(),
                reason
        );

        repo.save(event);

        // structured-ish log line for Loki queries
        log.info("eventType={} outcome={} userId={} email={} requestId={} traceId={} ip={} path={} method={} reason={}",
                type, outcome, userId, email, ctx.requestId(), ctx.traceId(), ctx.ip(), ctx.httpPath(), ctx.httpMethod(), reason);
    }
}
