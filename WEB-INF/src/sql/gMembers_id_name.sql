CREATE TABLE `gMembers_id_name` (
	`member_name`	varchar(20) NOT NULL,
	`internal_id`	int NOT NULL AUTO_INCREMENT,
	`rank` 			TINYINT UNSIGNED NOT NULL,
	PRIMARY KEY(member_name),
	KEY `internal_id` (`internal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;