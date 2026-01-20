-- Add expires_at to sessions to show session lifetime + expire old sessions safely
ALTER TABLE sessions
  ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ NULL;

-- Backfill existing rows with a sane default (14 days from created_at)
UPDATE sessions
SET expires_at = created_at + INTERVAL '14 days'
WHERE expires_at IS NULL;

-- Make it NOT NULL after backfill
ALTER TABLE sessions
  ALTER COLUMN expires_at SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_expires_at ON sessions(expires_at);
