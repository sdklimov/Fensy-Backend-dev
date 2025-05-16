--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка коллекций

insert into collections (author_id, title, description, allow_viewing_for, adult_content)
values (1, 'Collection number 1', 'Lorem ipsum...', 'any', false),
       (2, 'Collection number 2', 'Lorem ipsum...', 'followers', false),
       (2, 'Collection number 3', 'Lorem ipsum...', 'donors', false),
       (2, 'Collection number 4', 'Lorem ipsum...', 'none', false);
