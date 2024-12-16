-- V1__create_tables.sql

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    full_name   VARCHAR(100),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE credentials
(
    id        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id   UUID NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE authtokens
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token         VARCHAR(255) NOT NULL UNIQUE,
    user_id       UUID NOT NULL,
    creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_time   TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
