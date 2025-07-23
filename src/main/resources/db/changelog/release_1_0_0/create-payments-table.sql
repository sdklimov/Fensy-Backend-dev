--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы платежей

create table payments
(
    id              bigserial primary key,
    unique_id       uuid                     not null,
    subscription_id bigint                   not null references subscriptions (id) on delete cascade,
    amount_cents    double precision                  not null,
    currency        varchar(10)              not null default 'TON',
    payment_method  varchar(50),
    paid_at         timestamp with time zone          default null,
    status          varchar(20)              not null,
    created_at      timestamp with time zone not null default now(),
    updated_at      timestamp with time zone not null default now(),
    valid_until     timestamp with time zone not null
);

--rollback drop table payments;
