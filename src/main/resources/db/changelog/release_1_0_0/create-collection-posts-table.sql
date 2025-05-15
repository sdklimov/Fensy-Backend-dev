--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Связь коллекций с постами

create table collection_posts
(
    collection_id bigint not null references collections (id) on update cascade on delete cascade,
    post_id       bigint not null references posts (id) on update cascade on delete cascade,
    primary key (collection_id, post_id)
);

comment on table collection_posts is 'Связь коллекций с постами';

comment on column collection_posts.collection_id is 'ID коллекции';
comment on column collection_posts.post_id is 'ID поста';

--rollback drop table collection_posts;
