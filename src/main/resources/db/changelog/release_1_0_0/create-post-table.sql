--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Пост

create table posts
(
    id                bigserial primary key,
    original_post_id  bigint    references posts (id) on update cascade on delete set null,
    is_repost         boolean   not null default false,
    author_id         bigserial not null references users (id) on update cascade on delete cascade,
    title             text,
    content           text,
    allow_viewing_for text      not null,
    pinned            boolean   not null default false,
    adult_content     boolean   not null default false,
    created_at        timestamp not null default now(),
    updated_at        timestamp not null default now()
);

-- Индексы
create index posts_author_id_idx on posts (author_id);
create index posts_title_idx on posts (title);
create index posts_content_idx on posts (content);
create index posts_allow_viewing_for_idx on posts (allow_viewing_for);

create index if not exists posts_pgroonga_title_content_idx
    on posts using pgroonga (title, content);

-- Комментарии
comment on table posts is 'Публикации пользователей';

comment on column posts.id is 'Идентификатор поста';
comment on column posts.original_post_id is 'Идентификатор оригинального поста (для репостов или цитат)';
comment on column posts.is_repost is 'Флаг, указывающий, что пост является репостом';
comment on column posts.author_id is 'ID автора (внешний ключ на users)';
comment on column posts.title is 'Заголовок поста';
comment on column posts.content is 'Содержимое поста';
comment on column posts.allow_viewing_for is 'Кому разрешён просмотр: все, подписчики, никто, донаты';
comment on column posts.pinned is 'Закреплён ли пост';
comment on column posts.adult_content is 'Содержит ли контент 18+';
comment on column posts.created_at is 'Дата создания';
comment on column posts.updated_at is 'Дата последнего обновления';

--rollback drop table posts;
