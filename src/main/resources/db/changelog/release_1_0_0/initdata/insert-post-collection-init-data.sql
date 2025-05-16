--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка связей между постами и коллекциями

insert into post_collections (post_id, collection_id)
values (1, 1);