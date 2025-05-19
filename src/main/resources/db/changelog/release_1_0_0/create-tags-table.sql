--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Теги

create table tags
(
    id   bigserial primary key,
    name text not null unique
);

-- Индексы
create unique index tags_name_idx on tags (name);

-- Комментарии
comment on table tags is 'Теги, которыми можно помечать посты';

comment on column tags.id is 'Идентификатор тега';
comment on column tags.name is 'Уникальное имя тега';

--rollback drop table tags;
