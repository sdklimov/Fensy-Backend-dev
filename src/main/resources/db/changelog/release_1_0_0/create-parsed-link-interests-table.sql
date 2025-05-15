--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Связь между извлечёнными ссылками и интересами

create table parsed_link_interests
(
    parsed_link_id bigserial references parsed_links (id) on update cascade on delete cascade,
    interest_id    bigserial references interests (id) on update cascade on delete cascade,
    primary key (parsed_link_id, interest_id)
);

-- Комментарии
comment on table parsed_link_interests is 'Связь между извлечёнными ссылками и интересами';
comment on column parsed_link_interests.parsed_link_id is 'ID извлечённой ссылки';
comment on column parsed_link_interests.interest_id is 'ID интереса';

--rollback drop table parsed_link_interests;
