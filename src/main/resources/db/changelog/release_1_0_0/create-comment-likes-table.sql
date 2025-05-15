--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Связь лайков пользователей с комментариями

create table comment_likes
(
    comment_id bigint not null references comments (id) on update cascade on delete cascade,
    user_id    bigint not null references users (id) on update cascade on delete cascade,
    primary key (comment_id, user_id)
);

comment on table comment_likes is 'Связь лайков пользователей с комментариями';

comment on column comment_likes.comment_id is 'ID комментария';
comment on column comment_likes.user_id is 'ID пользователя, поставившего лайк';

--rollback drop table comment_likes;
