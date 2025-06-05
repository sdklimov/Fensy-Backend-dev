--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Создание таблицы Комментарии к постам, включая вложенные (ответы на комментарии)

create table comments
(
    id        bigserial primary key,
    content   text,
    author_id bigint not null references users (id) on update cascade on delete cascade,
    post_id   bigint not null references posts (id) on update cascade on delete cascade,
    parent_id bigint references comments (id) on update cascade on delete cascade
);

create index comments_post_id_idx on comments (post_id);
create index comments_author_id_idx on comments (author_id);

comment on table comments is 'Комментарии к постам, включая вложенные (ответы на комментарии)';

comment on column comments.id is 'Уникальный идентификатор комментария';
comment on column comments.content is 'Текст комментария';
comment on column comments.author_id is 'ID автора комментария';
comment on column comments.post_id is 'ID поста, к которому относится комментарий';
comment on column comments.parent_id is 'ID родительского комментария (если есть)';


--rollback drop table comments;
