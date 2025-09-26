--liquibase formatted sql

--changeset stnslv:create_media_assets_and_media_files_tables
--comment: Создание новых таблиц для медиа-активов и медиа-файлов, удаление старой таблицы files

-- Удаление старой таблицы files
DROP TABLE IF EXISTS files CASCADE;

-- Создание таблицы media_assets
CREATE TABLE media_assets
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    asset_type  VARCHAR(16)  NOT NULL,
    purpose     VARCHAR(32)  NOT NULL,
    created_by  BIGINT       NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Создание таблицы media_files
CREATE TABLE media_files
(
    id                UUID PRIMARY KEY,
    media_asset_id    UUID         NOT NULL REFERENCES media_assets (id) ON DELETE CASCADE,
    compression_size  VARCHAR(32) NOT NULL,
    storage_key       VARCHAR(256) NOT NULL,
    original_filename VARCHAR(256) NOT NULL,
    mime_type         VARCHAR(32) NOT NULL,
    size_bytes        BIGINT       NOT NULL,
    width             INTEGER,
    height            INTEGER,
    duration          BIGINT,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT unique_asset_compression UNIQUE (media_asset_id, compression_size)
);

-- Создание индексов для media_assets
CREATE INDEX media_assets_created_by_idx ON media_assets (created_by);
CREATE INDEX media_assets_asset_type_idx ON media_assets (asset_type);

-- Создание индексов для media_files
CREATE UNIQUE INDEX media_files_storage_key_idx ON media_files (storage_key);
CREATE INDEX media_files_media_asset_id_idx ON media_files (media_asset_id);
CREATE INDEX media_files_compression_size_idx ON media_files (compression_size);
CREATE INDEX media_files_mime_type_idx ON media_files (mime_type);

-- Комментарии для media_assets
COMMENT ON TABLE media_assets IS 'Медиа-активы - логические контейнеры для файлов';
COMMENT ON COLUMN media_assets.id IS 'Идентификатор медиа-актива';
COMMENT ON COLUMN media_assets.name IS 'Название медиа-актива';
COMMENT ON COLUMN media_assets.asset_type IS 'Тип медиа-актива';
COMMENT ON COLUMN media_assets.purpose IS 'Назначение медиа-актива';
COMMENT ON COLUMN media_assets.created_by IS 'Идентификатор пользователя, создавшего актив';
COMMENT ON COLUMN media_assets.created_at IS 'Дата и время создания';
COMMENT ON COLUMN media_assets.updated_at IS 'Дата и время последнего обновления';

-- Комментарии для media_files
COMMENT ON TABLE media_files IS 'Медиа-файлы - физические версии файлов, принадлежащие медиа-активам';
COMMENT ON COLUMN media_files.id IS 'Идентификатор медиа-файла';
COMMENT ON COLUMN media_files.media_asset_id IS 'Идентификатор родительского медиа-актива';
COMMENT ON COLUMN media_files.compression_size IS 'Размер сжатия файла (ORIGINAL, LARGE, MEDIUM, THUMBNAIL)';
COMMENT ON COLUMN media_files.storage_key IS 'Ключ в хранилище S3';
COMMENT ON COLUMN media_files.original_filename IS 'Оригинальное имя файла при загрузке';
COMMENT ON COLUMN media_files.mime_type IS 'MIME тип файла';
COMMENT ON COLUMN media_files.size_bytes IS 'Размер файла в байтах';
COMMENT ON COLUMN media_files.width IS 'Ширина изображения/видео в пикселях';
COMMENT ON COLUMN media_files.height IS 'Высота изображения/видео в пикселях';
COMMENT ON COLUMN media_files.duration IS 'Продолжительность видео/аудио в секундах';
COMMENT ON COLUMN media_files.created_at IS 'Дата и время создания';
COMMENT ON COLUMN media_files.updated_at IS 'Дата и время последнего обновления';

--rollback DROP TABLE media_files; DROP TABLE media_assets;
