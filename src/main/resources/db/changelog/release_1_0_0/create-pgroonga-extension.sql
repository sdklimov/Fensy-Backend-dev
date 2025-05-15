--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Включение расширения pgroonga

create extension if not exists pgroonga;


--rollback drop extension if exists pgroonga cascade;
