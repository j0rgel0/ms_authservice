-- V1__create_tables.sql
-- Script to create tables for Analytics, AuthTokens, and Credentials
CREATE TABLE authtokens
(
    token        VARCHAR(50) NOT NULL,
    username     VARCHAR(50),
    creationtime TIMESTAMP,
    expirytime   INTEGER     NOT NULL,
    PRIMARY KEY (token)
);

CREATE TABLE credentials
(
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50),
    PRIMARY KEY (username)
);
