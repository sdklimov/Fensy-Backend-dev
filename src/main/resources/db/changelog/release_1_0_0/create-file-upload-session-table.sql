--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы file_upload_session

create table file_upload_session
(
    id uuid primary key default gen_random_uuid(),
    user_id    bigint references users (id),
    is_closed bool not null default false,
    expired_at timestamp with time zone
);

create index file_upload_session_id_user_id_is_closed_idx on file_upload_session(id, user_id, is_closed);

comment on table post_attachments is 'Сессия загрузки файлов';

comment on column file_upload_session.id is 'Уникальный идентификатор сессии';
comment on column file_upload_session.expired_at is 'Время жизни сессии';
comment on column file_upload_session.is_closed is 'Флаг закрытой сессии';

--rollback drop table file_upload_session;
