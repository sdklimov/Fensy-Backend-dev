--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Вложения файлов, относящиеся к комментариям

create table comment_attachments
(
    id         bigserial primary key,
    comment_id bigint not null references comments (id) on update cascade on delete cascade,
    file_path  text   not null unique
);

comment on table comment_attachments is 'Вложения файлов, относящиеся к комментариям';

comment on column comment_attachments.id is 'Уникальный идентификатор вложения';
comment on column comment_attachments.comment_id is 'ID комментария, к которому прикреплен файл';
comment on column comment_attachments.file_path is 'Путь к файлу вложения';


--rollback drop table comment_attachments;
