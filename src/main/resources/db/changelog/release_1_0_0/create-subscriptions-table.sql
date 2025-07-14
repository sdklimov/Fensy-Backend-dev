--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы подписок

create table subscriptions
(
    id                bigserial primary key,
--     unique_id         uuid                     not null,
    subscriber_id     bigint                   not null references users (id) on delete cascade,
    target_id         bigint                   not null references users (id) on delete cascade,
    subscription_type varchar(10)              not null check (subscription_type in ('MONTHLY', 'YEARLY')),
    status            varchar(30)              not null,
    started_at        timestamp with time zone not null,
    expires_at        timestamp with time zone not null,
    created_at        timestamp with time zone not null default now(),
    updated_at        timestamp with time zone not null default now(),
    unique (subscriber_id, target_id),
    check (subscriber_id <> target_id)
);

create index on subscriptions (subscriber_id, status);

-- Комментарии
comment on table subscriptions is 'Подписки';

comment on column subscriptions.subscriber_id is 'Тот, кто подписывается';
comment on column subscriptions.target_id is 'На кого подписываются';

--rollback drop table subscriptions;
