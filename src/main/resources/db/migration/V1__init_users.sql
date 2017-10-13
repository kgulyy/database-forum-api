CREATE TABLE users (
  nickname    TEXT PRIMARY KEY,
  fullname    TEXT NOT NULL,
  email       TEXT NOT NULL UNIQUE,
  about       TEXT
);