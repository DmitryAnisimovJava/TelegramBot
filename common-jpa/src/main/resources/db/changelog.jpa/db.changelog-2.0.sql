--liquibase formatted sql

--changeset anisimovd:1
CREATE TABLE IF NOT EXISTS users_data.application_users
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY
        PRIMARY KEY,
    email            VARCHAR(255) UNIQUE,
    first_login_date TIMESTAMP,
    first_name       VARCHAR(255),
    is_active        BOOLEAN,
    last_name        VARCHAR(255),
    telegram_user_id BIGINT,
    user_state       VARCHAR(255),
    username         VARCHAR(255) UNIQUE
);
