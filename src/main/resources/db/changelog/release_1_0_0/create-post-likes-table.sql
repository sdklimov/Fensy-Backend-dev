--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы лайков постов

create table if not exists post_likes
(
    post_id    bigint                   not null references posts (id),
    user_id    bigint                   not null references users (id),
    created_at timestamp with time zone not null default now(),
    primary key (post_id, user_id)
);

create index if not exists post_id_idx on post_likes (post_id);
create index if not exists user_id_idx on post_likes (user_id);

--rollback drop table post_likes;
