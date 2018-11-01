CREATE TABLE `gMembers_id_name` (
	`internal_id`	int NOT NULL AUTO_INCREMENT,
	`member_name`	varchar(20) NOT NULL,
	`in_guild` 		TINYINT(1) NOT NULL,
	`rank` 			int NOT NULL,
	PRIMARY KEY(internal_id),
	UNIQUE (member_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;