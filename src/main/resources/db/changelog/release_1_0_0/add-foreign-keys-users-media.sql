--liquibase formatted sql

--changeset stnslv:add_foreign_keys_for_users_and_media_assets
--comment: Добавление внешних ключей между users и media_assets после создания обеих таблиц

-- Добавляем внешний ключ для media_assets.created_by -> users.id
ALTER TABLE media_assets ADD CONSTRAINT fk_media_assets_created_by
    FOREIGN KEY (created_by) REFERENCES users (id);

-- Добавляем внешний ключ для users.avatar -> media_assets.id
ALTER TABLE users ADD CONSTRAINT fk_users_avatar
    FOREIGN KEY (avatar) REFERENCES media_assets (id);

-- Создаем индекс для поля avatar
CREATE INDEX users_avatar_idx ON users (avatar);

--rollback ALTER TABLE users DROP CONSTRAINT fk_users_avatar; ALTER TABLE media_assets DROP CONSTRAINT fk_media_assets_created_by; DROP INDEX users_avatar_idx;
