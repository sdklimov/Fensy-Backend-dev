--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка постов

insert into posts (author_id, title, content, allow_viewing_for, pinned, adult_content)
values (1, 'Post number 1', 'Lorem ipsum...', 'ANY', true, false),
       (1, 'Post number 2', 'Lorem ipsum...', 'NONE', false, false),
       (1, 'Post number 3', 'Lorem ipsum...', 'DONORS', false, false),
       (1, 'Post number 4', 'Lorem ipsum...', 'FOLLOWERS', false, false);
