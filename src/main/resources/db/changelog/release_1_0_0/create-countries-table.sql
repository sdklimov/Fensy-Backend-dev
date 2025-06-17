--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Страны

create table countries
(
    id   bigserial primary key,
    code text not null,
    name text not null
);

-- Индекс
create unique index countries_code_idx on countries (code);

-- Комментарии
comment on table countries is 'Справочник стран';

comment on column countries.id is 'Идентификатор';
comment on column countries.code is 'Код страны (например, RU, US, FR)';
comment on column countries.name is 'Название страны';


--rollback drop table countries;
