--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Просмотры постов с уникальностью по IP-адресу

create table post_views
(
    id         bigserial primary key,
    post_id    bigint                   not null references posts (id) on update cascade on delete cascade,
    ip_address inet                     not null,
    viewed_at  timestamp with time zone not null default now()
);

-- уникальный индекс, чтобы один IP мог учитывать только один просмотр для одного поста
create unique index post_views_post_id_ip_address_idx on post_views (post_id, ip_address);

comment on table post_views is 'Просмотры постов с уникальностью по IP-адресу';
comment on column post_views.id is 'Уникальный идентификатор просмотра';
comment on column post_views.post_id is 'ID поста';
comment on column post_views.ip_address is 'IP-адрес посетителя';
comment on column post_views.viewed_at is 'Время просмотра';

--rollback drop table post_views;
