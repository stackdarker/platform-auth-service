package com.stackdarker.platform.auth.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuthAuditEventRepository extends JpaRepository<AuthAuditEventEntity, UUID> {
}