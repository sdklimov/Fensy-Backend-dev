--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Переводы интересов на разные языки

create table interest_translations
(
    interest_id bigint not null references interests (id) on update cascade on delete cascade,
    language_id bigint not null references languages (id) on update cascade on delete cascade,
    translation text   not null unique,
    primary key (interest_id, language_id)
);

-- Комментарии
comment on table interest_translations is 'Переводы интересов на разные языки';

comment on column interest_translations.interest_id is 'ID интереса (внешний ключ на interests)';
comment on column interest_translations.language_id is 'ID языка (внешний ключ на languages)';
comment on column interest_translations.translation is 'Переведённое название интереса';


--rollback drop table interest_translations;
