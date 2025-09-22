--liquibase formatted sql

--changeset stnslv:release_1_0_0
--comment: Создание таблицы Файлы

create table files
(
    id UUID PRIMARY KEY,
    storage_key VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,

    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,

    created_at  timestamp with time zone not null default now(),
    updated_at  timestamp with time zone not null default now()
);

-- индексы
create unique index files_s3_key_index on files (storage_key);

-- Комментарии
comment on table files is 'Файлы, загруженные пользователями';

comment on column files.id is 'Идентификатор';
comment on column files.storage_key is 'Ключ в S3';
comment on column files.original_filename is 'Оригинальное имя файла при загрузке';
comment on column files.mime_type is 'MIME тип файла';
comment on column files.size_bytes is 'Размер файла в байтах';

--rollback drop table files;
