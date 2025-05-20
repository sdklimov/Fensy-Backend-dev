--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Интересы пользователя

create table user_interests
(
    user_id     bigint           not null references users (id) on update cascade on delete cascade,
    interest_id bigint           not null references interests (id) on update cascade on delete cascade,
    weight      DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    primary key (user_id, interest_id)
);

-- Комментарии
comment on table user_interests is 'Интересы пользователя (связь многие ко многим)';

comment on column user_interests.user_id is 'ID пользователя';
comment on column user_interests.interest_id is 'ID интереса';
comment on column user_interests.weight is 'Вес интереса (значимость от 0.00 до 9.99)';

--rollback drop table user_interests;
