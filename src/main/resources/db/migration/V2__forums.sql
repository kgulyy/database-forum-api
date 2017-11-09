CREATE TABLE forums (
  slug    TEXT PRIMARY KEY,
  title   TEXT NOT NULL,
  author  TEXT NOT NULL REFERENCES users (nickname),
  posts   BIGINT,
  threads INTEGER
);

CREATE INDEX idx_forums_slug ON forums (LOWER(slug));
CREATE INDEX idx_forums_author ON forums (author);