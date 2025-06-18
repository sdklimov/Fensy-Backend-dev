--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Отозванных токенов

create table if not exists revoked_tokens
(
    id         bigserial primary key,
    token      varchar(512)             not null,
    expires_at timestamp with time zone not null,
    user_id    bigint                   not null,
    revoked_at timestamp with time zone not null default now(),
    constraint fk_user foreign key (user_id) references users (id)
);

create index idx_revoked_tokens_token on revoked_tokens (token);
create index idx_revoked_tokens_expires_at on revoked_tokens (expires_at);

--rollback drop table revoked_tokens;
