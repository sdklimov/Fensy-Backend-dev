--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Языки

create table languages
(
    id   bigserial primary key,
    code text not null,
    name text not null
);

-- Индекс
create unique index languages_code_idx on languages (code);

-- Комментарии
comment on table languages is 'Справочник языков';

comment on column languages.id is 'Идентификатор';
comment on column languages.code is 'Код языка (например, en, ru, fr)';
comment on column languages.name is 'Название языка (например, English, Русский)';

--rollback drop table languages;
