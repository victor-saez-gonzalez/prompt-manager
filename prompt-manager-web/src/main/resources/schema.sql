DROP TABLE IF EXISTS prompt_tags;
DROP TABLE IF EXISTS prompts;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS users;


CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255),
  password VARCHAR(255),
  provider VARCHAR(50) NOT NULL,
  provider_id VARCHAR(255)
);

CREATE TABLE category (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  description VARCHAR(255),
  user_id UUID NOT NULL,
  CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE tag (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  description VARCHAR(255),
  user_id UUID NOT NULL,
  CONSTRAINT fk_tag_user FOREIGN KEY (user_id) REFERENCES users(id)
);
