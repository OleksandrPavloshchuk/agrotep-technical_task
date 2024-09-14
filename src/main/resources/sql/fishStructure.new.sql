DROP TABLE IF EXISTS `fish`;
CREATE TABLE `fish`
(
    `id`              int    NOT NULL AUTO_INCREMENT,
    `catch_date`      datetime(6)  DEFAULT NULL,
    `image_file_name` varchar(255) DEFAULT NULL,
    `name`            varchar(255) DEFAULT NULL,
    `price`           double NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `fish_picture`;
CREATE TABLE `fish_picture`
(
    `image_file_name` varchar(255) NOT NULL,
    `fish_id` int NOT NULL,
    PRIMARY KEY (`fish_id`, `image_file_name`)
);

ALTER TABLE `fish_picture`
ADD FOREIGN KEY `to_fish`(`fish_id`)
REFERENCES `fish`(`id`)
ON DELETE RESTRICT
ON UPDATE RESTRICT;