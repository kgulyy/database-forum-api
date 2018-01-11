CREATE TABLE IF NOT EXISTS posts (
  id       BIGSERIAL PRIMARY KEY,
  parent_id   BIGINT  NOT NULL,
  author   CITEXT  NOT NULL REFERENCES users (nickname),
  message  TEXT    NOT NULL,
  is_edited BOOLEAN NOT NULL,
  forum    CITEXT  NOT NULL REFERENCES forums (slug),
  thread_id   INTEGER NOT NULL REFERENCES threads (id),
  created  TIMESTAMPTZ DEFAULT now(),
  path     BIGINT []
);

CREATE INDEX IF NOT EXISTS idx_posts_author ON posts (author);
CREATE INDEX IF NOT EXISTS idx_posts_thread ON posts (thread_id);
CREATE INDEX IF NOT EXISTS idx_posts_threadId_postIdDesc ON posts (thread_id, id DESC);
CREATE INDEX IF NOT EXISTS idx_posts_postId_path ON posts (id, path);
CREATE INDEX IF NOT EXISTS idx_posts_threadId_pathDesc ON posts (thread_id, path DESC);
CREATE INDEX IF NOT EXISTS idx_posts_threadId_parentIdDesc_postIdDesc ON posts (thread_id, parent_id DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_posts_threadId_pathFirst ON posts (thread_id, (path[1]));
CREATE INDEX IF NOT EXISTS idx_posts_threadId_parentIdDesc_path_postIdDesc ON posts (thread_id, parent_id DESC, path, id DESC);
