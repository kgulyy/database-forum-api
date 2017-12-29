CREATE TABLE IF NOT EXISTS posts (
  id       BIGSERIAL PRIMARY KEY,
  parent   BIGINT  NOT NULL,
  author   CITEXT  NOT NULL REFERENCES users (nickname),
  message  TEXT    NOT NULL,
  isEdited BOOLEAN NOT NULL,
  forum    CITEXT  NOT NULL REFERENCES forums (slug),
  thread   INTEGER NOT NULL REFERENCES threads (id),
  created  TIMESTAMPTZ,
  path     BIGINT []
);

CREATE INDEX IF NOT EXISTS idx_posts_parent ON posts (parent);
CREATE INDEX IF NOT EXISTS idx_posts_author ON posts (author);
CREATE INDEX IF NOT EXISTS idx_posts_forum ON posts (forum);
CREATE INDEX IF NOT EXISTS idx_posts_thread ON posts (thread);