CREATE TYPE Gender AS ENUM ('male', 'female', 'undefined');
CREATE TYPE MediaClass AS ENUM ('photo', 'video');

CREATE TABLE users (
  id VARCHAR PRIMARY KEY,
  username VARCHAR UNIQUE NOT NULL,
  email VARCHAR UNIQUE NOT NULL,
  password_hash VARCHAR NOT NULL,
  is_private BOOLEAN NOT NULL,
  full_name VARCHAR NOT NULL,
  gender GENDER,
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

CREATE TABLE media (
  id VARCHAR PRIMARY KEY,
  class MEDIACLASS NOT NULL,
  url VARCHAR NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  deleted_at TIMESTAMP
);

CREATE TABLE posts (
  id VARCHAR PRIMARY KEY,
  user_id VARCHAR REFERENCES users (id),
  caption TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  blocked_at TIMESTAMP,
  deleted_at TIMESTAMP
);

CREATE TABLE posts_media (
  id BIGSERIAL PRIMARY KEY,
  post_id VARCHAR REFERENCES posts (id),
  media_id VARCHAR REFERENCES media (id)
);

CREATE TABLE posts_likes (
  id BIGSERIAL PRIMARY KEY,
  post_id VARCHAR REFERENCES posts (id),
  user_id VARCHAR REFERENCES users (id),
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE posts_tags (
  id BIGSERIAL PRIMARY KEY,
  post_id VARCHAR REFERENCES posts (id),
  user_id VARCHAR REFERENCES users (id),
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE posts_bookmarks (
  id BIGSERIAL PRIMARY KEY,
  post_id VARCHAR REFERENCES posts (id),
  user_id VARCHAR REFERENCES users (id),
  created_at TIMESTAMP NOT NULL
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
