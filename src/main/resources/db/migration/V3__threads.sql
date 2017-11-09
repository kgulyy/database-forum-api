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