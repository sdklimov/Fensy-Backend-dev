--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Пользователи

create table users
(
    id            bigserial primary key,
    is_verified      boolean   not null default false,
    full_name     text,
    username      text unique,
    email         text unique,
    avatar        uuid,
    bio           text,
    location      text,
    role          text      not null default 'USER',
    website       text,
    telegram_id   text unique,
    ton_wallet_id text unique,
    yandex_id     text unique,
    vk_id         text unique,
    is_active     boolean   not null default true,
    last_login_at timestamp with time zone not null default now(),
    country_id    bigint    not null references countries (id),
    language_id   bigint    not null references languages (id),
    created_at    timestamp with time zone not null default now(),
    updated_at    timestamp with time zone not null default now()
);

-- индексы
create unique index users_username_idx on users (username);
create unique index users_email_idx on users (email);
create index users_role_idx on users (role);
create unique index users_telegram_id_idx on users (telegram_id);
create unique index users_ton_wallet_id_idx on users (ton_wallet_id);
create unique index users_yandex_id_idx on users (yandex_id);
create unique index users_vk_id_idx on users (vk_id);

-- Комментарии
comment on table users is 'Пользователи';

comment on column users.id is 'Идентификатор';
comment on column users.is_verified is 'Признак верифицированного пользователя';
comment on column users.full_name is 'Полное имя';
comment on column users.username is 'Имя пользователя';
comment on column users.email is 'Email';
comment on column users.avatar is 'Ссылка на аватар';
comment on column users.bio is 'Краткая биография';
comment on column users.location is 'Местоположение пользователя';
comment on column users.role is 'Роль пользователя (user или pro)';
comment on column users.website is 'Личный веб-сайт';
comment on column users.telegram_id is 'Telegram ID';
comment on column users.ton_wallet_id is 'TON кошелёк';
comment on column users.yandex_id is 'Yandex ID';
comment on column users.vk_id is 'VK ID';
comment on column users.is_active is 'Признак активного пользователя';
comment on column users.last_login_at is 'Дата последнего входа';
comment on column users.created_at is 'Дата создания записи';
comment on column users.updated_at is 'Дата последнего обновления';

--rollback drop table users;
