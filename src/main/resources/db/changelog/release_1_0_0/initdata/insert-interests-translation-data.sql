--liquibase formatted sql

--changeset evbogdanov:release_1_0_0
--comment: Вставка интересов на разных языках

insert into interest_translations (interest_id, language_id, translation)
values (1, 1, 'Одежда'),
       (2, 1, 'Луки'),
       (3, 1, 'Стилисты'),
       (4, 1, 'Искусство'),
       (5, 1, 'Образы'),
       (6, 1, 'Культура'),
       (7, 1, 'Машины'),
       (8, 1, 'Гаджеты'),
       (9, 1, 'Музыка'),
       (10, 1, 'Фотографии'),
       (11, 1, 'Видеоигры'),
       (12, 1, 'Кино'),
       (13, 1, 'Природа'),
       (14, 1, 'Спорт'),
       (15, 1, 'Технологии'),
       (16, 1, 'Еда'),
       (17, 1, 'Айти'),
       (18, 1, 'Дизайн');

insert into interest_translations (interest_id, language_id, translation)
values (1, 2, 'Clothes'),
       (2, 2, 'Looks'),
       (3, 2, 'Stylists'),
       (4, 2, 'Art'),
       (5, 2, 'Images'),
       (6, 2, 'Culture'),
       (7, 2, 'Cars'),
       (8, 2, 'Gadgets'),
       (9, 2, 'Music'),
       (10, 2, 'Photos'),
       (11, 2, 'Video Games'),
       (12, 2, 'Cinema'),
       (13, 2, 'Nature'),
       (14, 2, 'Sports'),
       (15, 2, 'Technologies'),
       (16, 2, 'Food'),
       (17, 2, 'IT'),
       (18, 2, 'Design');



