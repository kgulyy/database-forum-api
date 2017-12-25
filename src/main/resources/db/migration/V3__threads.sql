CREATE TABLE threads (
  id      SERIAL PRIMARY KEY,
  title   TEXT    NOT NULL,
  author  TEXT    NOT NULL REFERENCES users (nickname),
  forum   TEXT    NOT NULL REFERENCES forums (slug),
  message TEXT    NOT NULL,
  votes   INTEGER NOT NULL,
  slug    TEXT UNIQUE,
  created TIMESTAMPTZ
);

CREATE INDEX idx_threads_slug ON threads (LOWER(slug));
CREATE INDEX idx_threads_author ON threads (author);
CREATE INDEX idx_threads_forum ON threads (forum);