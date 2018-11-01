CREATE TABLE `guild_info` (
	`name` 			varchar(50) NOT NULL,
	`lastModified`	bigint(20) NOT NULL,
	`battlegroup` 	varchar(50) NOT NULL,
	`level` 		int NOT NULL,
	`side`			int NOT NULL,
	`achievementPoints`	bigint(20) NOT NULL,
	PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `gMembers_id_name` (
	`internal_id`	int NOT NULL AUTO_INCREMENT,
	`member_name`	varchar(20) NOT NULL,
	`in_guild` 		TINYINT(1) NOT NULL,
	`rank` 			int NOT NULL,
	PRIMARY KEY(internal_id),
	UNIQUE (member_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `character_info` (
	`internal_id`		int NOT NULL,
	`realm`				varchar(50) NOT NULL,
	`battlegroup` 		varchar(50) NOT NULL,
	`class`				int NOT NULL,
	`race`				int NOT NULL,
	`gender`			int NOT NULL,
	`level`				int NOT NULL,
	`achievementPoints`	bigint(20) NOT NULL,
	`thumbnail`			varchar(70) NOT NULL,
	`calcClass`			varchar(2) NOT NULL,
	`faction`			int NOT NULL,
	`totalHonorableKills` bigint(20) NOT NULL,
	`guild_name`		varchar(50) NOT NULL,
	`lastModified`		bigint(20) NOT NULL,
	PRIMARY KEY(internal_id),
	FOREIGN KEY(internal_id) REFERENCES gMembers_id_name(internal_id),
	FOREIGN KEY(class) REFERENCES playable_class(id),
	FOREIGN KEY(race) REFERENCES races(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;