CREATE TABLE posts (
  id       BIGSERIAL PRIMARY KEY,
  parent   BIGINT  NOT NULL,
  author   TEXT    NOT NULL REFERENCES users (nickname),
  message  TEXT    NOT NULL,
  isEdited BOOLEAN NOT NULL,
  forum    TEXT    NOT NULL REFERENCES forums (slug),
  thread   INTEGER NOT NULL REFERENCES threads (id),
  created  TIMESTAMP WITH TIME ZONE
);