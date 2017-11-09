CREATE TABLE posts (
  id       BIGSERIAL PRIMARY KEY,
  parent   BIGINT  NOT NULL,
  author   TEXT    NOT NULL REFERENCES users (nickname),
  message  TEXT    NOT NULL,
  isEdited BOOLEAN NOT NULL,
  forum    TEXT    NOT NULL REFERENCES forums (slug),
  thread   INTEGER NOT NULL REFERENCES threads (id),
  created  TIMESTAMPTZ,
  path     BIGINT []
);

CREATE INDEX idx_posts_parent ON posts (parent);
CREATE INDEX idx_posts_author ON posts (author);
CREATE INDEX idx_posts_forum ON posts (forum);
CREATE INDEX idx_posts_thread ON posts (thread);