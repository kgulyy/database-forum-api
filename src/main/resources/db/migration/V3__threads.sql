CREATE TABLE threads (
  id      SERIAL PRIMARY KEY,
  title   TEXT    NOT NULL,
  author  CITEXT  NOT NULL REFERENCES users (nickname),
  forum   CITEXT  NOT NULL REFERENCES forums (slug),
  message TEXT    NOT NULL,
  votes   INTEGER NOT NULL,
  slug    CITEXT  UNIQUE,
  created TIMESTAMPTZ
);

CREATE INDEX idx_threads_author ON threads (author);
CREATE INDEX idx_threads_forum ON threads (forum);