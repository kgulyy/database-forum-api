CREATE TABLE IF NOT EXISTS threads (
  id      SERIAL PRIMARY KEY,
  title   TEXT    NOT NULL,
  author  CITEXT  NOT NULL,
  forum   CITEXT  NOT NULL,
  message TEXT    NOT NULL,
  votes   INTEGER NOT NULL DEFAULT 0,
  slug    CITEXT UNIQUE    DEFAULT NULL,
  created TIMESTAMPTZ      DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_threads_author
  ON threads (author);
CREATE INDEX IF NOT EXISTS idx_threads_slug
  ON threads (slug);
CREATE INDEX IF NOT EXISTS idx_threads_slug_id
  ON threads (slug, id);
CREATE INDEX IF NOT EXISTS idx_threads_forum
  ON threads (forum);
CREATE INDEX IF NOT EXISTS idx_threads_forum_created
  ON threads (forum, created);