--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы file_upload_session_to_file

create table file_upload_session_to_file
(
    session_id uuid references file_upload_session (id),
    file_id    uuid references media_assets (id),
    primary key (session_id, file_id)
);

comment on table post_attachments is 'Файлы прикрепленные к сессии';

--rollback drop table file_upload_session;
