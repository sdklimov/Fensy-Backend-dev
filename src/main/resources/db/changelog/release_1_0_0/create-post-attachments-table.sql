--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Вложенные файлы к постам

create table post_attachments
(
    id         bigserial primary key,
    post_id    bigint not null references posts (id) on update cascade on delete cascade,
    file_id    uuid references media_assets (id)
);

comment on table post_attachments is 'Вложенные файлы к постам';

comment on column post_attachments.id is 'Уникальный идентификатор вложения';
comment on column post_attachments.post_id is 'ID поста, к которому прикреплено вложение';
comment on column post_attachments.file_id is 'ID файла вложения';

--rollback drop table post_attachments;
