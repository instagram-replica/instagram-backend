CREATE TABLE users (
  id VARCHAR PRIMARY KEY,
  username VARCHAR UNIQUE NOT NULL,
  email VARCHAR UNIQUE NOT NULL,
  password_hash VARCHAR NOT NULL,
  is_private BOOLEAN NOT NULL,
  full_name VARCHAR NOT NULL,
  gender VARCHAR NOT NULL,
  bio VARCHAR,
  phone_number VARCHAR,
  profile_picture_url VARCHAR,
  website_url VARCHAR,
  verified_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  blocked_at TIMESTAMP,
  deleted_at TIMESTAMP
);



CREATE TABLE users_follows (
  id BIGSERIAL PRIMARY KEY,
  follower_id VARCHAR REFERENCES users (id),
  followed_id VARCHAR REFERENCES users (id),
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE users_reports (
  id BIGSERIAL PRIMARY KEY,
  reporter_id VARCHAR REFERENCES users (id),
  reported_id VARCHAR REFERENCES users (id),
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE users_blocks (
  id BIGSERIAL PRIMARY KEY,
  blocker_id VARCHAR REFERENCES users (id),
  blocked_id VARCHAR REFERENCES users (id),
  created_at TIMESTAMP NOT NULL
);
