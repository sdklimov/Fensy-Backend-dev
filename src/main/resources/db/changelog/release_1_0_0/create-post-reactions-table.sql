--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы реакций постов

create table if not exists post_reactions
(
    id         bigserial primary key,
    post_id    bigint                   not null references posts (id),
    user_id    bigint                   not null references users (id),
    emoji      varchar(10),
    created_at timestamp with time zone not null default now()
);

create index if not exists post_id_user_id_idx on post_reactions (post_id, user_id);
create index if not exists post_id_idx on post_reactions (post_id);
create index if not exists user_id_idx on post_reactions (user_id);

--rollback drop table post_reactions;
