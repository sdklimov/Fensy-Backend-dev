--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Обложки коллекций

create table collection_covers
(
    id            bigserial primary key,
    collection_id bigint not null references collections (id) on update cascade on delete cascade,
    image         text   not null unique
);

comment on table collection_covers is 'Обложки коллекций';

comment on column collection_covers.id is 'ID обложки';
comment on column collection_covers.collection_id is 'ID коллекции';
comment on column collection_covers.image is 'Уникальный путь или URL изображения обложки';

--rollback drop table collection_covers;
