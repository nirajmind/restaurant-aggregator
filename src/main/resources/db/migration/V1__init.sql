-- V1__init.sql
CREATE TABLE source (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(64) NOT NULL UNIQUE,
  base_url TEXT,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE city (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  country VARCHAR(64) NOT NULL,
  tz VARCHAR(64) NOT NULL DEFAULT 'Asia/Dubai',
  UNIQUE (name, country)
);

CREATE TABLE cuisine (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE restaurant (
  id BIGSERIAL PRIMARY KEY,
  canonical_name VARCHAR(256) NOT NULL,
  address TEXT,
  latitude NUMERIC(9,6),
  longitude NUMERIC(9,6),
  city_id BIGINT REFERENCES city(id),
  price_range SMALLINT CHECK (price_range BETWEEN 1 AND 5),
  avg_rating NUMERIC(2,1) CHECK (avg_rating BETWEEN 0 AND 5),
  phone VARCHAR(48),
  website TEXT,
  is_open BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_restaurant_city ON restaurant(city_id);
CREATE INDEX idx_restaurant_geo ON restaurant(latitude, longitude);

CREATE TABLE restaurant_cuisine (
  restaurant_id BIGINT REFERENCES restaurant(id) ON DELETE CASCADE,
  cuisine_id BIGINT REFERENCES cuisine(id) ON DELETE RESTRICT,
  PRIMARY KEY (restaurant_id, cuisine_id)
);

CREATE TABLE tag (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(64) NOT NULL UNIQUE
);
CREATE TABLE restaurant_tag (
  restaurant_id BIGINT REFERENCES restaurant(id) ON DELETE CASCADE,
  tag_id BIGINT REFERENCES tag(id) ON DELETE RESTRICT,
  PRIMARY KEY (restaurant_id, tag_id)
);

CREATE TABLE restaurant_source_map (
  id BIGSERIAL PRIMARY KEY,
  restaurant_id BIGINT REFERENCES restaurant(id) ON DELETE CASCADE,
  source_id BIGINT REFERENCES source(id) ON DELETE RESTRICT,
  external_id VARCHAR(128) NOT NULL,
  external_slug VARCHAR(256),
  raw_hash VARCHAR(64),
  UNIQUE (source_id, external_id)
);

CREATE TABLE menu_item (
  id BIGSERIAL PRIMARY KEY,
  restaurant_id BIGINT REFERENCES restaurant(id) ON DELETE CASCADE,
  name VARCHAR(256) NOT NULL,
  description TEXT,
  price NUMERIC(10,2),
  currency VARCHAR(3) DEFAULT 'AED',
  available BOOLEAN DEFAULT TRUE
);
CREATE INDEX idx_menu_item_restaurant ON menu_item(restaurant_id);

CREATE TABLE review (
  id BIGSERIAL PRIMARY KEY,
  restaurant_id BIGINT REFERENCES restaurant(id) ON DELETE CASCADE,
  rating SMALLINT CHECK (rating BETWEEN 1 AND 5),
  content TEXT,
  author VARCHAR(128),
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO roles(name) VALUES('ROLE_USER') ON CONFLICT DO NOTHING;
INSERT INTO roles(name) VALUES('ROLE_RESTAURANT_OWNER') ON CONFLICT DO NOTHING;
INSERT INTO roles(name) VALUES('ROLE_ADMIN') ON CONFLICT DO NOTHING;