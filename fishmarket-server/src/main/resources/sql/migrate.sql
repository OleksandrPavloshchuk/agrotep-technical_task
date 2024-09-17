DROP TABLE IF EXISTS `fish_picture`;
CREATE TABLE `fish_picture`
(
    `id`              int    NOT NULL AUTO_INCREMENT,
    `image_file_name` varchar(255) NOT NULL,
    `fish_id`         int NOT NULL,
    `content_type`    varchar(255) NOT NULL,
    `content_size`    long NOT NULL,
    `content`         longblob,
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

-- TODO write Java migration application, which saves all pictures from file system to