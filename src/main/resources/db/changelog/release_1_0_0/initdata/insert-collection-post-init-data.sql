--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка связей коллекций с постами
insert into collection_posts (collection_id, post_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (1, 4);