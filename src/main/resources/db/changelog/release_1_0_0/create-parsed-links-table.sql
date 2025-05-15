--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Ссылки, извлечённые из содержимого постов (например, статьи, продукты и т.п.)

create table parsed_links
(
    id          bigserial primary key,
    post_id     bigserial not null references posts (id) on update cascade on delete cascade,
    type        text      not null,
    link        text      not null,
    picture     text,
    title       text      not null,
    description text,
    price       decimal(15, 2),
    currency    text default '₽'
);

-- Комментарии
comment on table parsed_links is 'Ссылки, извлечённые из содержимого постов (например, статьи, продукты и т.п.)';

comment on column parsed_links.id is 'Идентификатор';
comment on column parsed_links.post_id is 'ID поста, к которому относится ссылка';
comment on column parsed_links.type is 'Тип извлечённой ссылки: статья, подкаст, товар и т.д.';
comment on column parsed_links.link is 'Сама ссылка';
comment on column parsed_links.picture is 'URL изображения';
comment on column parsed_links.title is 'Заголовок по ссылке';
comment on column parsed_links.description is 'Описание по ссылке';
comment on column parsed_links.price is 'Цена (если применимо)';
comment on column parsed_links.currency is 'Валюта (по умолчанию рубли)';


--rollback drop table parsed_links;
