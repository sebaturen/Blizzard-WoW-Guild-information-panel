CREATE TABLE `update_timeline` (
	`id` 			int NOT NULL AUTO_INCREMENT,
	`type`			int NOT NULL,
	`update_time`	datetime NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `wow_token` (
    `last_updated_timestamp`    bigint(20) NOT NULL,
    `price`                     int NOT NULL,
    PRIMARY KEY(last_updated_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;