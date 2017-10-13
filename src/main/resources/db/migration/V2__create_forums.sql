CREATE TABLE forums (
  slug    TEXT PRIMARY KEY,
  title   TEXT NOT NULL,
  author  TEXT NOT NULL REFERENCES users (nickname),
  posts   BIGINT,
  threads INTEGER
);