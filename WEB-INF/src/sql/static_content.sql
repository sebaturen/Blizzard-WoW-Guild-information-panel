CREATE TABLE `races` (
	`id` 	int NOT NULL,
	`mask` 	int NOT NULL,
	`side`	varchar(50) NOT NULL,
	`name`	varchar(50) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playable_class` (
	`id` 	int NOT NULL,
	`en_US`	varchar(50) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;