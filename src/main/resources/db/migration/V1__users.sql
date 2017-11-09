CREATE TABLE users (
  nickname TEXT PRIMARY KEY,
  fullname TEXT NOT NULL,
  email    TEXT NOT NULL UNIQUE,
  about    TEXT
);

CREATE INDEX idx_users_nickname ON users (LOWER(nickname));
CREATE INDEX idx_users_email ON users (LOWER(email));