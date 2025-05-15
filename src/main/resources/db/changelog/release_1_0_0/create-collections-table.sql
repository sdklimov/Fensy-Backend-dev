--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Коллекции постов пользователя

create table collections
(
    id                bigserial primary key,
    author_id         bigint    not null references users (id) on update cascade on delete cascade,
    title             text      not null,
    description       text      not null,
    allow_viewing_for text      not null,
    adult_content     boolean   not null default false,
    created_at        timestamp not null default now(),
    updated_at        timestamp not null default now()
);

-- Индексы
create index on collections (author_id);
create index on collections (title);
create index on collections (description);
create index on collections (allow_viewing_for);
create index if not exists collections_pgroonga_text_idx
    on collections using pgroonga (
                                   title,
                                   description
        );

-- Комментарии
comment on table collections is 'Коллекции постов пользователя';

comment on column collections.author_id is 'ID пользователя, автор коллекции';
comment on column collections.title is 'Название коллекции';
comment on column collections.description is 'Описание коллекции';
comment on column collections.allow_viewing_for is 'Кому доступна коллекция: any, followers, none, donors';
comment on column collections.adult_content is 'Содержит ли коллекция контент для взрослых';

--rollback drop table collections;
