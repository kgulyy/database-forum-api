CREATE TABLE IF NOT EXISTS votes (
  id         SERIAL PRIMARY KEY,
  thread_id  INTEGER  NOT NULL,
  user_id    INTEGER  NOT NULL,
  vote_value SMALLINT NOT NULL
);

ALTER TABLE votes
  ADD CONSTRAINT thread_user UNIQUE (
  thread_id,
  user_id
);