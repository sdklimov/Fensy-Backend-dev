--liquibase formatted sql

--changeset stnslv:insert_default_avatars_init_data
--comment: Вставка дефолтных аватарок в систему

INSERT INTO users (id, full_name, username, email, role, is_verified, is_active, country_id, language_id, created_at, updated_at, last_login_at)
VALUES (0, 'System', 'system', 'system@fensy.dev', 'ADMIN', true, true, 1, 1, NOW(), NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Вставляем медиа-активы для дефолтных аватарок (30 штук)
WITH avatar_numbers AS (
    SELECT generate_series(1, 30) as num
),
avatar_uuids AS (
    SELECT
        num,
        ('00000000-0000-0000-0000-0000000000' || LPAD(num::TEXT, 2, '0'))::UUID as avatar_uuid
    FROM avatar_numbers
)
INSERT INTO media_assets (
    id,
    name,
    asset_type,
    purpose,
    created_by,
    created_at,
    updated_at
)
SELECT
    avatar_uuid,
    'Default Avatar ' || LPAD(num::TEXT, 2, '0'),
    'IMAGE',
    'AVATAR',
    0,
    NOW(),
    NOW()
FROM avatar_uuids;

-- Вставляем thumbnail версии для всех аватарок
WITH avatar_numbers AS (
    SELECT generate_series(1, 30) as num
),
avatar_uuids AS (
    SELECT
        num,
        ('00000000-0000-0000-0000-0000000000' || LPAD(num::TEXT, 2, '0'))::UUID as avatar_uuid
    FROM avatar_numbers
)
INSERT INTO media_files (
    id,
    media_asset_id,
    compression_size,
    storage_key,
    original_filename,
    mime_type,
    size_bytes,
    width,
    height,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    avatar_uuid,
    'THUMBNAIL',
    'system/avatars/' || avatar_uuid || '/thumbnail.png',
    'default_avatar.png',
    'image/png',
    2500,
    50,
    50,
    NOW(),
    NOW()
FROM avatar_uuids;

-- Вставляем medium версии для всех аватарок
WITH avatar_numbers AS (
    SELECT generate_series(1, 30) as num
),
avatar_uuids AS (
    SELECT
        num,
        ('00000000-0000-0000-0000-0000000000' || LPAD(num::TEXT, 2, '0'))::UUID as avatar_uuid
    FROM avatar_numbers
)
INSERT INTO media_files (
    id,
    media_asset_id,
    compression_size,
    storage_key,
    original_filename,
    mime_type,
    size_bytes,
    width,
    height,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    avatar_uuid,
    'MEDIUM',
    'system/avatars/' || avatar_uuid || '/medium.png',
    'default_avatar.png',
    'image/png',
    15000,
    150,
    150,
    NOW(),
    NOW()
FROM avatar_uuids;

-- Вставляем large версии для всех аватарок
WITH avatar_numbers AS (
    SELECT generate_series(1, 30) as num
),
avatar_uuids AS (
    SELECT
        num,
        ('00000000-0000-0000-0000-0000000000' || LPAD(num::TEXT, 2, '0'))::UUID as avatar_uuid
    FROM avatar_numbers
)
INSERT INTO media_files (
    id,
    media_asset_id,
    compression_size,
    storage_key,
    original_filename,
    mime_type,
    size_bytes,
    width,
    height,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    avatar_uuid,
    'LARGE',
    'system/avatars/' || avatar_uuid || '/large.png',
    'default_avatar.png',
    'image/png',
    125000,
    500,
    500,
    NOW(),
    NOW()
FROM avatar_uuids;

-- Вставляем original версии для всех аватарок
WITH avatar_numbers AS (
    SELECT generate_series(1, 30) as num
),
avatar_uuids AS (
    SELECT
        num,
        ('00000000-0000-0000-0000-0000000000' || LPAD(num::TEXT, 2, '0'))::UUID as avatar_uuid
    FROM avatar_numbers
)
INSERT INTO media_files (
    id,
    media_asset_id,
    compression_size,
    storage_key,
    original_filename,
    mime_type,
    size_bytes,
    width,
    height,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    avatar_uuid,
    'ORIGINAL',
    'system/avatars/' || avatar_uuid || '/original.png',
    'default_avatar.png',
    'image/png',
    500000,
    1024,
    1024,
    NOW(),
    NOW()
FROM avatar_uuids;

--rollback DELETE FROM media_files WHERE storage_key LIKE 'system/avatars/00000000-0000-0000-0000-0000000000%'; DELETE FROM media_assets WHERE name LIKE 'Default Avatar %' AND asset_type = 'IMAGE' AND created_by = 0;
