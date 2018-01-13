CREATE TABLE IF NOT EXISTS forums (
  id              SERIAL PRIMARY KEY,
  slug            CITEXT  NOT NULL UNIQUE,
  title           TEXT    NOT NULL,
  author_id       INTEGER NOT NULL REFERENCES users (id),
  author_nickname CITEXT  NOT NULL,
  posts           BIGINT  DEFAULT 0,
  threads         INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_forums_slug
  ON forums (slug);
CREATE INDEX IF NOT EXISTS idx_forums_slug_id
  ON forums (slug, id);

CREATE TABLE IF NOT EXISTS forum_users (
  user_id  INTEGER,
  forum_id INTEGER,
  PRIMARY KEY (user_id, forum_id)
);