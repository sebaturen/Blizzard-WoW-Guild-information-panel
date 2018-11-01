CREATE TABLE `challenges` (
	`challenge_id`	int NOT NULL AUTO_INCREMENT,
	`map_name`		varchar(50) NOT NULL,
	PRIMARY KEY(challenge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `challenge_groups` (
	`group_id`		int NOT NULL AUTO_INCREMENT,
	`challenge_id`	int NOT NULL,
	`time_date`		DATETIME NOT NULL,
	`time_hours`	int NOT NULL,
	`time_minutes`	int NOT NULL, 
	`time_seconds`	int NOT NULL,
	`time_milliseconds`	int NOT NULL,
	`is_positive`	TINYINT(1) NOT NULL,
	PRIMARY KEY(group_id),
	FOREIGN KEY(challenge_id) REFERENCES challenges(challenge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `challenge_group_members` (
	`member_in_group_id`	int NOT NULL AUTO_INCREMENT,
	`internal_member_id`	int NOT NULL,
	`group_id`				int NOT NULL,
	`spec_name`				varchar(50) NOT NULL,
	`spec_role`				varchar(10) NOT NULL,
	PRIMARY KEY(member_in_group_id),
	FOREIGN KEY(internal_member_id) REFERENCES gMembers_id_name(internal_id),
	FOREIGN KEY(group_id) REFERENCES challenge_groups(group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;