CREATE TABLE IF NOT EXISTS forums (
  slug            CITEXT PRIMARY KEY,
  title           TEXT   NOT NULL,
  author_id       INT    NOT NULL REFERENCES users (id),
  author_nickname CITEXT NOT NULL,
  posts           BIGINT,
  threads         INTEGER
);

CREATE INDEX IF NOT EXISTS idx_forums_author_nickname ON forums (author_nickname);