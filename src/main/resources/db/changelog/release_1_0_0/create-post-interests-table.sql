--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Связь между постами и интересами (many-to-many)'

create table post_interests
(
    post_id     bigint not null references posts (id) on update cascade on delete cascade,
    interest_id bigint not null references interests (id) on update cascade on delete cascade,
    primary key (post_id, interest_id)
);

-- Комментарии
comment on table post_interests is 'Связь между постами и интересами (many-to-many)';
comment on column post_interests.post_id is 'ID поста (внешний ключ на posts)';
comment on column post_interests.interest_id is 'ID интереса (внешний ключ на interests)';


--rollback drop table post_interests;
