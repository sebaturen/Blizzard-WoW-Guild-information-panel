CREATE TABLE `update_timeline` (
	`id` 			int NOT NULL AUTO_INCREMENT,
	`type`			TINYINT NOT NULL,
	`update_time`	datetime NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;