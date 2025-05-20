--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Настройки пользователя

create table user_settings
(
    id                     bigserial primary key,
    user_id                bigint    not null unique references users (id) on update cascade on delete cascade,
    allow_messages_from    text      not null default 'ANY',
    notifications_on_email boolean   not null default true,
    ad_on_email            boolean   not null default true,
    created_at             timestamp with time zone not null default now(),
    updated_at             timestamp with time zone not null default now()
);

-- Индексы
create unique index user_settings_userid_idx on user_settings (user_id);
create index user_settings_allow_messages_from_idx on user_settings (allow_messages_from);
create index user_settings_notifications_on_email_idx on user_settings (notifications_on_email);
create index user_settings_ad_on_email_idx on user_settings (ad_on_email);

-- Комментарии
comment on table user_settings is 'Настройки пользователя';

comment on column user_settings.id is 'Идентификатор';
comment on column user_settings.user_id is 'ID пользователя (внешний ключ на users.id)';
comment on column user_settings.allow_messages_from is 'Кто может отправлять сообщения: any, followers, none';
comment on column user_settings.notifications_on_email is 'Включены ли уведомления на email';
comment on column user_settings.ad_on_email is 'Получать ли рекламные письма';
comment on column user_settings.created_at is 'Дата создания записи';
comment on column user_settings.updated_at is 'Дата последнего обновления';

--rollback drop table user_settings;
