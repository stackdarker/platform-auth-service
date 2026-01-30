CREATE TABLE IF NOT EXISTS auth_audit_events (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  event_type TEXT NOT NULL,
  outcome TEXT NOT NULL,

  user_id UUID NULL,
  email TEXT NULL,

  request_id TEXT NULL,
  trace_id TEXT NULL,

  ip TEXT NULL,
  user_agent TEXT NULL,

  http_method TEXT NULL,
  http_path TEXT NULL,

  failure_reason TEXT NULL,

  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_auth_audit_events_created_at ON auth_audit_events(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_auth_audit_events_user_id ON auth_audit_events(user_id);
CREATE INDEX IF NOT EXISTS idx_auth_audit_events_event_type ON auth_audit_events(event_type);
