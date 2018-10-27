CREATE TABLE `guild_info` (
	`name` 			varchar(50) DEFAULT NULL,
	`lastModified`	bigint(20) NOT NULL,
	`battlegroup` 	varchar(50) DEFAULT NULL,
	`level` 		int(11) NOT NULL,
	`side`			int(11) NOT NULL,
	`achievementPoints`	bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `guild_info`
  ADD PRIMARY KEY (`name`);
COMMIT;