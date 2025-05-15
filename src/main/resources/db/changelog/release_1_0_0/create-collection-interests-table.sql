--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Связь коллекций с интересами

create table collection_interests
(
    collection_id bigint not null references collections (id) on update cascade on delete cascade,
    interest_id   integer not null references interests (id) on update cascade on delete cascade,
    primary key (collection_id, interest_id)
);

comment on table collection_interests is 'Связь коллекций с интересами';

comment on column collection_interests.collection_id is 'ID коллекции';
comment on column collection_interests.interest_id is 'ID интереса';

--rollback drop table collection_interests;
