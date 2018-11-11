CREATE TABLE `challenges` (
    `id`                int NOT NULL AUTO_INCREMENT,
    `map_name`          varchar(50) NOT NULL,
    `bronze_hours`          int not null,
    `bronze_minutes`        int not null,
    `bronze_seconds`        int not null,
    `bronze_milliseconds`   int not null,
    `silver_hours`          int not null,
    `silver_minutes`        int not null,
    `silver_seconds`        int not null,
    `silver_milliseconds`   int not null,
    `gold_hours`          int not null,
    `gold_minutes`        int not null,
    `gold_seconds`        int not null,
    `gold_milliseconds`   int not null,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `challenge_groups` (
    `group_id`      int NOT NULL AUTO_INCREMENT,
    `challenge_id`  int NOT NULL,
    `time_date`     DATETIME NOT NULL,
    `time_hours`    int NOT NULL,
    `time_minutes`  int NOT NULL, 
    `time_seconds`  int NOT NULL,
    `time_milliseconds` int NOT NULL,
    `is_positive`   TINYINT(1) NOT NULL,
    PRIMARY KEY(group_id),
    FOREIGN KEY(challenge_id) REFERENCES challenges(id),
    UNIQUE (challenge_id, time_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `challenge_group_members` (
    `member_in_group_id`    int NOT NULL AUTO_INCREMENT,
    `internal_member_id`    int NOT NULL,
    `group_id`      int NOT NULL,
    `spec_id`       int NOT NULL,
    PRIMARY KEY(member_in_group_id),
    FOREIGN KEY(internal_member_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(group_id) REFERENCES challenge_groups(group_id),
    FOREIGN KEY(spec_id) REFERENCES specs(id),
    UNIQUE (internal_member_id, group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;