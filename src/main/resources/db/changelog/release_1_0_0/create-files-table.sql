--liquibase formatted sql

--changeset stnslv:release_1_0_0
--comment: Создание таблицы Файлы

create table files
(
    id UUID PRIMARY KEY,
    s3_key VARCHAR(255)  NOT NULL,

    context_type VARCHAR(50) NOT NULL,
    context_id  VARCHAR(255),

    created_at  timestamp with time zone not null default now(),
    updated_at  timestamp with time zone not null default now()
);

-- индексы
create unique index files_s3_key_index on files (s3_key);

-- Комментарии
comment on table files is 'Файлы, загруженные пользователями';

comment on column files.id is 'Идентификатор';
comment on column files.s3_key is 'Ключ в S3';
comment on column files.context_type is 'Тип контента, к которому привязан файл (например, "USER_AVATAR", "POST_IMAGE")';
comment on column files.context_id is 'ID сущности, к которой он привязан (например, ID пользователя или ID поста)';

--rollback drop table files;
