--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы refresh_tokens

create table refresh_tokens
(
    id         uuid primary key                  default gen_random_uuid(),
    user_id    bigint                   not null references users (id),
    token_hash text                     not null,
    jwt_id     TEXT                     NOT NULL,
    expires_at timestamp with time zone not null,
    created_at timestamp with time zone not null default now(),
    revoked    boolean                  not null default false
);

create index idx_refresh_tokens_user_id on refresh_tokens (user_id);
create index idx_refresh_tokens_token_hash on refresh_tokens (token_hash);

--rollback drop refresh_tokens;
