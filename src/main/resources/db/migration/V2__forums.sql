CREATE TABLE forums (
  slug    CITEXT PRIMARY KEY,
  title   TEXT   NOT NULL,
  author  CITEXT NOT NULL REFERENCES users (nickname),
  posts   BIGINT,
  threads INTEGER
);

CREATE INDEX idx_forums_author ON forums (author);