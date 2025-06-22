--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы подписок

create table subscriptions
(
    subscriber_id bigint                   not null references users (id) on delete cascade,
    target_id     bigint                   not null references users (id) on delete cascade,
    created_at    timestamp with time zone not null default now(),
    primary key (subscriber_id, target_id),
    check (subscriber_id <> target_id)
);

create index on subscriptions (subscriber_id);

-- Комментарии
comment on table subscriptions is 'Подписки';

comment on column subscriptions.subscriber_id is 'Тот, кто подписывается';
comment on column subscriptions.target_id is 'На кого подписываются';

--rollback drop table subscriptions;
