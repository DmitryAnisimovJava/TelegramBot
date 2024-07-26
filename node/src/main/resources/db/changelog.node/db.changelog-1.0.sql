--liquibase formatted sql

--changeset anisimovd:1
CREATE TABLE IF NOT EXISTS audit.raw_data
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY
        PRIMARY KEY,
    event JSONB
);