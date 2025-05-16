--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка интересов

insert into interests (name)
values ('clothes'),
       ('looks'),
       ('stylists'),
       ('art'),
       ('images'),
       ('culture'),
       ('cars'),
       ('gadgets'),
       ('music'),
       ('photos'),
       ('video games'),
       ('cinema'),
       ('nature'),
       ('sports'),
       ('technologies'),
       ('food'),
       ('information technologies'),
       ('design');


