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
    `battle_tag`    varchar(50),
    `access_token`  varchar(50),
    `guild_rank`    TINYINT DEFAULT -1,
    `wowinfo`       TINYINT(1) DEFAULT 0,
    PRIMARY KEY(id),
    UNIQUE(battle_tag)
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
    `id`            INT NOT NULL AUTO_INCREMENT,
    `slug`          VARCHAR(50) NOT NULL,
    `name`          VARCHAR(50) NOT NULL,
    `total_boss`    TINYINT DEFAULT -1,
    PRIMARY KEY(id),
    UNIQUE(`slug`)
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

CREATE TABLE `items` (
    `id`    INT NOT NULL,
    `name`  VARCHAR(50) NOT NULL,
    `icon`  VARCHAR(50) NOT NULL,
    `gemInfo_bonus_name`    VARCHAR(50),
    `gemInfo_type`          VARCHAR(50),
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO items (`id`, `name`, `icon`) VALUES (0, "NULL ITEM", "NULL ITEM");

CREATE TABLE `items_member` (
    `id`                    INT NOT NULL AUTO_INCREMENT,
    `member_id`             INT NOT NULL,
    `item_id`               INT NOT NULL,
    `post_item`             VARCHAR(20) NOT NULL,
    `ilevel`                INT NOT NULL,
    `stats`                 TINYTEXT NOT NULL, 
    `armor`                 INT NOT NULL,
    `context`               VARCHAR(50) NOT NULL,
    `azerita_level`         INT NOT NULL,
    `azerita_power`         TINYTEXT,
    `tooltipGem_id`         INT,
    `toolTipEnchant_id`     INT,
    PRIMARY KEY(id),
    UNIQUE(member_id, item_id, post_item),
    FOREIGN KEY(member_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(item_id) REFERENCES items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `member_status` (
    `member_id` INT NOT NULL,
    `health`    INT NOT NULL,
    `powerType` VARCHAR(20) NOT NULL,
    `power`     INT NOT NULL,
    `str`       INT NOT NULL,
    `agi`       INT NOT NULL,
    `int`       INT NOT NULL,
    `sta`       INT NOT NULL,
    `speedRating`       INT NOT NULL,
    `speedRatingBonus`  DOUBLE NOT NULL,
    `crit`          DOUBLE NOT NULL,
    `critRating`    INT NOT NULL,
    `haste`         DOUBLE NOT NULL,
    `hasteRating`   INT NOT NULL,
    `hasteRatingPercent`    DOUBLE NOT NULL,
    `mastery`           DOUBLE NOT NULL,
    `masteryRating`     INT NOT NULL,
    `leech`             INT NOT NULL,
    `leechRating`       INT NOT NULL,
    `leechRatingBonus`  INT NOT NULL,
    `versatility`       INT NOT NULL,
    `versatilityDamageDoneBonus`    DOUBLE NOT NULL,
    `versatilityHealingDoneBonus`   DOUBLE NOT NULL,
    `versatilityDamageTakenBonus`   DOUBLE NOT NULL,
    `avoidanceRating`       INT NOT NULL,
    `avoidanceRatingBonus`  DOUBLE NOT NULL,
    `spellPen`          INT NOT NULL,
    `spellCrit`         DOUBLE NOT NULL,
    `spellCritRating`   INT NOT NULL,
    `mana5`             INT NOT NULL,
    `mana5Combat`       INT NOT NULL,
    `armor`             INT NOT NULL,
    `dodge`             DOUBLE NOT NULL,
    `dodgeRating`       INT NOT NULL,
    `parry`             DOUBLE NOT NULL,
    `parryRating`       INT NOT NULL,
    `block`             DOUBLE NOT NULL,
    `blockRating`       INT NOT NULL,
    `mainHandDmgMin`    INT NOT NULL,
    `mainHandDmgMax`    INT NOT NULL,
    `mainHandSpeed`     DOUBLE NOT NULL,
    `mainHandDps`       DOUBLE NOT NULL,
    `offHandDmgMin`     INT NOT NULL,
    `offHandDmgMax`     INT NOT NULL,
    `offHandSpeed`      DOUBLE NOT NULL,
    `offHandDps`        DOUBLE NOT NULL,
    `rangedDmgMin`      INT NOT NULL,
    `rangedDmgMax`      INT NOT NULL,
    `rangedSpeed`       DOUBLE NOT NULL,
    `rangedDps`         DOUBLE NOT NULL,
    PRIMARY KEY(member_id),
    FOREIGN KEY(member_id) REFERENCES gMembers_id_name(internal_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
