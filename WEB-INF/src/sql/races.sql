CREATE TABLE `races` (
	`id` 	TINYINT NOT NULL,
	`mask` 	int NOT NULL,
	`side`	varchar(50) NOT NULL,
	`name`	varchar(50) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;