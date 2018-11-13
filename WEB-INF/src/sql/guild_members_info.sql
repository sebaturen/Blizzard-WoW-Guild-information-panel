CREATE TABLE `guild_info` (
    `id`                int NOT NULL AUTO_INCREMENT,
    `name`              varchar(50) NOT NULL,
    `realm`             varchar(50) NOT NULL,
    `lastModified`      bigint(20) NOT NULL,
    `battlegroup`       varchar(50) NOT NULL,
    `level`             int NOT NULL,
    `side`              int NOT NULL,
    `achievementPoints` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`name`, `realm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `users` (
    `id`            INT NOT NULL AUTO_INCREMENT,
    `email`         varchar(50) NOT NULL,
    `password`      varchar(50) NOT NULL,
    `battle_tag`    varchar(50),
    `access_token`  varchar(50),
    `guild_rank`    TINYINT DEFAULT -1,
    `wowinfo`       TINYINT(1) DEFAULT 0,
    PRIMARY KEY(id),
    UNIQUE(email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `gMembers_id_name` (
    `internal_id`   int NOT NULL AUTO_INCREMENT,
    `member_name`   varchar(20) NOT NULL,
    `realm`         varchar(50) NOT NULL,
    `in_guild`      TINYINT(1) NOT NULL,
    `rank`          int,
    `user_id`       int,
    PRIMARY KEY(internal_id),
    FOREIGN KEY(user_id) REFERENCES users(id),
    UNIQUE (member_name, realm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `character_info` (
    `internal_id`       int NOT NULL,
    `battlegroup`       varchar(50) NOT NULL,
    `class`             int NOT NULL,
    `race`              int NOT NULL,
    `gender`            int NOT NULL,
    `level`             int NOT NULL,
    `achievementPoints` bigint(20) NOT NULL,
    `thumbnail`         varchar(70) NOT NULL,
    `calcClass`         varchar(2) NOT NULL,
    `faction`           int NOT NULL,
    `totalHonorableKills`   bigint(20) NOT NULL,
    `guild_name`            varchar(50) NOT NULL,
    `lastModified`          bigint(20) NOT NULL,
    PRIMARY KEY(internal_id),
    FOREIGN KEY(internal_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(class) REFERENCES playable_class(id),
    FOREIGN KEY(race) REFERENCES races(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `specs` (
    `id`            int NOT NULL AUTO_INCREMENT,
    `member_id`     int NOT NULL,
    `name`          varchar(50) NOT NULL,
    `role`          varchar(20) NOT NULL,
    `enable`        TINYINT(1) NOT NULL,
    `tier_0`        int,
    `tier_1`        int,
    `tier_2`        int,
    `tier_3`        int,
    `tier_4`        int,
    `tier_5`        int,
    `tier_6`        int,
    PRIMARY KEY(id),
    FOREIGN KEY(member_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(tier_0) REFERENCES spells(id),
    FOREIGN KEY(tier_1) REFERENCES spells(id),
    FOREIGN KEY(tier_2) REFERENCES spells(id),
    FOREIGN KEY(tier_3) REFERENCES spells(id),
    FOREIGN KEY(tier_4) REFERENCES spells(id),
    FOREIGN KEY(tier_5) REFERENCES spells(id),
    FOREIGN KEY(tier_6) REFERENCES spells(id),
    UNIQUE (member_id,name,role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_news` (
    `id`                    INT NOT NULL AUTO_INCREMENT,
    `type`                  TINYINT NOT NULL,
    `member_id`             INT NOT NULL,
    `timestamp`             bigint(20) NOT NULL,
    `item_id`               INT,
    `guild_achivement_id`   INT,
    `player_achivement_id`  INT,
    PRIMARY KEY(id),
    FOREIGN KEY(member_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(guild_achivement_id) REFERENCES guild_achievements_list(id),
    FOREIGN KEY(player_achivement_id) REFERENCES (id),
    FOREIGN KEY(item_id) REFERENCES (id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `raids` (
    `id`    INT NOT NULL AUTO_INCREMENT,
    `guild_id`  INT NOT NULL,
    `slug`  VARCHAR(50) NOT NULL,
    `name`  varchar(50) NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_raids` (
    `id`    INT NOT NULL AUTO_INCREMENT,
    `guild_id`  INT NOT NULL,
    `raid_id`   INT NOT NULL,
    PRIMARY KEY(`id`),
    FOREIGN KEY(guild_id) REFERENCES guild_info(id),
    FOREIGN KEY(raid_id) REFERENCES raids(id)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `raid_dificults` (
    `difi_id`       INT NOT NULL AUTO_INCREMENT,
    `raid_id`       INT NOT NULL,
    `name`          varchar(50) NOT NULL,
    `rank_world`    INT,
    `rank_region`   INT,
    `rank_realm`    INT,
    PRIMARY KEY(difi_id),
    FOREIGN KEY(raid_id) REFERENCES raids(id),
    UNIQUE(`raid_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `raid_dificult_bosses` (
    `r_d_boss_id`       INT NOT NULL AUTO_INCREMENT,
    `boss_id`           INT NOT NULL,
    `difi_id`           INT NOT NULL,
    `firstDefeated`     datetime NOT NULL,
    `itemLevelAvg`      DOUBLE NOT NULL,
    `artifactPowerAvg`  DOUBLE,
    PRIMARY KEY(r_d_boss_id),
    FOREIGN KEY(boss_id) REFERENCES boss_list(id),
    FOREIGN KEY(difi_id) REFERENCES raid_dificults(difi_id),
    UNIQUE (boss_id,difi_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;