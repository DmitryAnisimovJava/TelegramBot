--liquibase formatted sql

--changeset anisimovd:1
CREATE SCHEMA IF NOT EXISTS telegram_data;

--changeset anisimovd:2
CREATE TABLE IF NOT EXISTS telegram_data.app_document
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY
        PRIMARY KEY,
    binary_file      BYTEA,
    doc_name         VARCHAR(255),
    file_size        BIGINT,
    mime_type        VARCHAR(255),
    telegram_file_id VARCHAR(255)
);

--changeset anisimovd:3
CREATE TABLE IF NOT EXISTS telegram_data.app_photo
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY
        PRIMARY KEY,
    binary_file      BYTEA,
    file_size        INTEGER,
    telegram_file_id VARCHAR(255)
);