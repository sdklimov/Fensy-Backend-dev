--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Связь между постами и коллекциями

create table post_collections
(
    post_id       bigint  not null references posts (id),
    collection_id integer not null references collections (id),
    primary key (post_id, collection_id)
);

comment on table post_collections is 'связь между постами и коллекциями';
comment on column post_collections.post_id is 'id поста';
comment on column post_collections.collection_id is 'id коллекции';

--rollback drop table post_collections;
