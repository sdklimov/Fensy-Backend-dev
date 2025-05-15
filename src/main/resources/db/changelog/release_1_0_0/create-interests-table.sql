--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Интересы

create table interests
(
    id   bigserial primary key,
    name text not null unique
);

-- Индекс
create unique index interests_name_idx on interests (name);

-- Комментарии
comment on table interests is 'Интересы';

comment on column interests.id is 'Идентификатор';
comment on column interests.name is 'Название интереса (уникальное)';

--rollback drop table interests;
