CREATE EXTENSION IF NOT EXISTS CITEXT;

CREATE TABLE IF NOT EXISTS users (
  id       SERIAL PRIMARY KEY,
  nickname CITEXT NOT NULL UNIQUE COLLATE UCS_BASIC,
  fullname TEXT   NOT NULL,
  email    CITEXT NOT NULL UNIQUE,
  about    TEXT
);

CREATE INDEX IF NOT EXISTS idx_users_nickname
  ON users (nickname);