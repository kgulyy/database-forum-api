CREATE TABLE IF NOT EXISTS votes (
  nickname  CITEXT REFERENCES users (nickname),
  thread_id INTEGER  NOT NULL REFERENCES threads (id),
  voice     SMALLINT NOT NULL,
  PRIMARY KEY (nickname, thread_id)
);