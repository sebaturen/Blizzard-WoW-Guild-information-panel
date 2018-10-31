CREATE TABLE `guild_info` (
	`name` 			varchar(50) NOT NULL,
	`lastModified`	bigint(20) NOT NULL,
	`battlegroup` 	varchar(50) NOT NULL,
	`level` 		int NOT NULL,
	`side`			int NOT NULL,
	`achievementPoints`	bigint(20) NOT NULL,
	PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;