--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Связь между постами и тегами (many-to-many)

create table post_tags
(
    post_id bigserial not null references posts (id) on update cascade on delete cascade,
    tag_id  bigserial not null references tags (id) on update cascade on delete cascade,
    primary key (post_id, tag_id)
);

-- Комментарии
comment on table post_tags is 'Связь между постами и тегами (many-to-many)';

comment on column post_tags.post_id is 'ID поста (внешний ключ на posts)';
comment on column post_tags.tag_id is 'ID тега (внешний ключ на tags)';

--rollback drop table post_tags;
