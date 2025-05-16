--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка языков
insert into languages (code, name)
values ('ru', 'русский'),
       ('en', 'english');




