CREATE TABLE users (
  nickname CITEXT PRIMARY KEY,
  fullname TEXT   NOT NULL,
  email    CITEXT NOT NULL UNIQUE,
  about    TEXT
);