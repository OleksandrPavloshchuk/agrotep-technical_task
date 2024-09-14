CREATE TABLE `fish_picture`
(
    `id`              int    NOT NULL AUTO_INCREMENT,
    `image_file_name` varchar(255) NOT NULL,
    `fish_id`         int NOT NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE `fish_picture`
    ADD INDEX `fish_and_file`(`fish_id`, `image_file_name`);

ALTER TABLE `fish_picture`
    ADD FOREIGN KEY `to_fish`(`fish_id`)
        REFERENCES `fish`(`id`)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT;

INSERT INTO `fish_picture`(`fish_id`, `image_file_name`)
SELECT `id`, `image_file_name` FROM fish;

ALTER TABLE `fish`
DROP COLUMN `image_file_name`;