--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка пользователей

insert into users (username, country_id, language_id, yandex_id, last_login_at, created_at, updated_at)
values ('user1', 1, 1, '1', now(), now(), now()),
       ('user2', 7, 2, '2', now(), now(), now());

insert into user_interests(user_id, interest_id)
values (1, 1),
       (2, 2);

insert into user_settings(user_id) values (1), (2)