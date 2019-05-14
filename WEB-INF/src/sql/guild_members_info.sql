CREATE TABLE `guild_info` (
    `id`                int NOT NULL AUTO_INCREMENT,
    `name`              varchar(50) NOT NULL,
    `realm`             varchar(50) NOT NULL,
    `realm_slug`        varchar(50) NOT NULL,
    `lastModified`      bigint(20) NOT NULL,
    `battlegroup`       varchar(50) NOT NULL,
    `level`             int NOT NULL,
    `side`              int NOT NULL,
    `achievementPoints` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`name`, `realm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `gMembers_id_name` (
    `internal_id`   int NOT NULL AUTO_INCREMENT,
    `member_name`   varchar(20) NOT NULL,
    `realm`         varchar(50) NOT NULL,
    `in_guild`      TINYINT(1) NOT NULL,
    `rank`          int,
    `user_id`       int,
    `isDelete`      TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY(internal_id),
    UNIQUE (member_name, realm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `users` (
    `id`                INT NOT NULL AUTO_INCREMENT,
    `battle_tag`        varchar(50),
    `access_token`      varchar(50),
    `discord_user_id`   varchar(50),
    `guild_rank`        TINYINT DEFAULT -1,
    `main_character`    INT,
    `wowinfo`           TINYINT(1) DEFAULT 0,
    `last_login`         DATETIME,
    `last_alters_update`    DATETIME,
    PRIMARY KEY(id),
    UNIQUE(battle_tag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
ALTER TABLE `gMembers_id_name` ADD FOREIGN KEY(user_id) REFERENCES users(id);
ALTER TABLE `users` ADD FOREIGN KEY(main_character) REFERENCES gMembers_id_name(internal_id);

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
    `bestMythicPlusScore`   TEXT,
    `mythicPlusScores`      TEXT,
    `guild_name`            varchar(50) NOT NULL,
    `lastModified`          bigint(20) NOT NULL,
    PRIMARY KEY(internal_id),
    FOREIGN KEY(internal_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(`class`) REFERENCES playable_class(id),
    FOREIGN KEY(race) REFERENCES playable_races(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `character_specs` (
    `id`            int NOT NULL AUTO_INCREMENT,
    `member_id`     int NOT NULL,
    `spec_id`       INT NOT NULL,
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
    FOREIGN KEY(spec_id) REFERENCES playable_spec(id),
    FOREIGN KEY(tier_0) REFERENCES spells(id),
    FOREIGN KEY(tier_1) REFERENCES spells(id),
    FOREIGN KEY(tier_2) REFERENCES spells(id),
    FOREIGN KEY(tier_3) REFERENCES spells(id),
    FOREIGN KEY(tier_4) REFERENCES spells(id),
    FOREIGN KEY(tier_5) REFERENCES spells(id),
    FOREIGN KEY(tier_6) REFERENCES spells(id),
    UNIQUE (member_id,spec_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_news` (
    `id`                    INT NOT NULL AUTO_INCREMENT,
    `type`                  VARCHAR(20) NOT NULL,
    `member_id`             INT NOT NULL,
    `context`               VARCHAR(50) NOT NULL,
    `timestamp`             DATETIME NOT NULL,
    `item_id`               INT,
    `guild_achievement_id`   INT,
    `player_achievement_id`  INT,
    PRIMARY KEY(id),
    FOREIGN KEY(member_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(guild_achievement_id) REFERENCES guild_achievements_list(id),
    FOREIGN KEY(player_achievement_id) REFERENCES player_achievement_list(id),
    FOREIGN KEY(item_id) REFERENCES items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_achievements` (
    `achievement_id`     INT NOT NULL,
    `time_completed`         datetime NOT NULL,
    PRIMARY KEY(achievement_id),
    FOREIGN KEY(achievement_id) REFERENCES guild_achievements_list(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_raid_dificults` (
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

CREATE TABLE `guild_raid_dificult_bosses` (
    `r_d_boss_id`       INT NOT NULL AUTO_INCREMENT,
    `boss_id`           INT NOT NULL,
    `difi_id`           INT NOT NULL,
    `firstDefeated`     datetime NOT NULL,
    `itemLevelAvg`      DOUBLE NOT NULL,
    `artifactPowerAvg`  DOUBLE,
    PRIMARY KEY(r_d_boss_id),
    FOREIGN KEY(boss_id) REFERENCES boss_list(id),
    FOREIGN KEY(difi_id) REFERENCES guild_raid_dificults(difi_id),
    UNIQUE (boss_id,difi_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_challenges` (
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

CREATE TABLE `guild_challenge_groups` (
    `group_id`      int NOT NULL AUTO_INCREMENT,
    `challenge_id`  int NOT NULL,
    `time_date`     DATETIME NOT NULL,
    `time_hours`    int NOT NULL,
    `time_minutes`  int NOT NULL,
    `time_seconds`  int NOT NULL,
    `time_milliseconds` int NOT NULL,
    `is_positive`   TINYINT(1) NOT NULL,
    PRIMARY KEY(group_id),
    FOREIGN KEY(challenge_id) REFERENCES guild_challenges(id),
    UNIQUE (challenge_id, time_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_challenge_group_members` (
    `member_in_group_id`    int NOT NULL AUTO_INCREMENT,
    `internal_member_id`    int NOT NULL,
    `group_id`      int NOT NULL,
    `character_spec_id`       int NOT NULL,
    PRIMARY KEY(member_in_group_id),
    FOREIGN KEY(internal_member_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(group_id) REFERENCES guild_challenge_groups(group_id),
    FOREIGN KEY(character_spec_id) REFERENCES character_specs(id),
    UNIQUE (internal_member_id, group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `character_items` (
    `id`                    INT NOT NULL AUTO_INCREMENT,
    `member_id`             INT NOT NULL,
    `item_id`               INT NOT NULL,
    `quality`               INT NOT NULL,
    `post_item`             VARCHAR(20) NOT NULL,
    `ilevel`                INT NOT NULL,
    `stats`                 TINYTEXT NOT NULL,
    `armor`                 INT NOT NULL,
    `context`               VARCHAR(50) NOT NULL,
    `azerite_level`         INT NOT NULL,
    `azerite_power`         TEXT,
    `tooltipGem_id`         INT,
    `toolTipEnchant_id`     INT,
    PRIMARY KEY(id),
    UNIQUE(member_id, item_id, post_item),
    FOREIGN KEY(member_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(item_id) REFERENCES items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `character_stats` (
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

CREATE TABLE `auction_items` (
    `auc`   INT NOT NULL,
    `item`  INT NOT NULL,
    `buyout`    bigint NOT NULL,
    `bid`       bigint NOT NULL,
    `quantity`  INT NOT NULL,
    `timeLeft`  VARCHAR(20) NOT NULL,
    `owner`     VARCHAR(20) NOT NULL,
    `ownerRealm`    VARCHAR(20) NOT NULL,
    `context`   INT NOT NULL,
    `rand`      INT NOT NULL,
    `status`    TINYINT(1) NOT NULL,
    `auc_date`  DATETIME NOT NULL,
    PRIMARY KEY (auc),
    FOREIGN KEY(item) REFERENCES items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `auction_history` (
    `id`    INT NOT NULL AUTO_INCREMENT,
    `item`  INT NOT NULL,
    `unique_price`  bigint NOT NULL,
    `context`   INT NOT NULL,
    `date`  DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY(item) REFERENCES items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `polls` (
    `id`            INT NULL AUTO_INCREMENT,
    `user_id`       INT NOT NULL,
    `poll_question` TEXT NOT NULL,
    `min_rank`      INT,
    `multi_select`  TINYINT(1) NOT NULL,
    `can_add_more_option`  TINYINT(1) NOT NULL,
    `start_date`    DATETIME NOT NULL,
    `is_limit_date` TINYINT(1) NOT NULL,
    `end_date`	    DATETIME,
    `isEnable`      TINYINT(1) NOT NULL DEFAULT 1,
    `isHide`        TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(min_rank) REFERENCES guild_rank(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `poll_options` (
    `id`         INT NOT NULL AUTO_INCREMENT,
    `poll_id`    INT NOT NULL,
    `option_txt` TINYTEXT NOT NULL,
    `owner_id`   INT NOT NULL,
    `date`       DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY(poll_id) REFERENCES polls(id),
    FOREIGN KEY(owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `poll_option_result` (
    `id`             INT NOT NULL AUTO_INCREMENT,
    `poll_option_id` INT NOT NULL,
    `owner_id`       INT NOT NULL,
    `date`           DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY(owner_id) REFERENCES users(id),
    FOREIGN KEY(poll_option_id) REFERENCES poll_options(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `keystone_dungeon` (
    `id`        INT NOT NULL,
    `map_id`    INT NOT NULL,
    `name`  VARCHAR(50) NOT NULL,
    `slug`  VARCHAR(50) NOT NULL,
    `keystone_upgrades_1`   bigint(20) NOT NULL,
    `keystone_upgrades_2`   bigint(20) NOT NULL,
    `keystone_upgrades_3`   bigint(20) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `keystone_dungeon_run` (
    `id`                    int NOT NULL AUTO_INCREMENT,
    `completed_timestamp`   BIGINT NOT NULL,
    `duration`              BIGINT NOT NULL,
    `keystone_level`        INT NOT NULL,
    `keystone_dungeon_id`   int not null,
    `is_complete_in_time`   TINYINT(1) NOT NULL,
    `key_affixes`           TEXT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(`completed_timestamp`,`duration`, `keystone_level`, `keystone_dungeon_id`, `is_complete_in_time`),
    FOREIGN KEY(keystone_dungeon_id) REFERENCES keystone_dungeon(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `keystone_dungeon_run_members` (
    `id`                        int NOT NULL AUTO_INCREMENT,
    `keystone_dungeon_run_id`   INT NOT NULL,
    `character_internal_id`     INT NOT NULL,
    `character_spec_id`         INT NOT NULL,
    `character_item_level`      INT NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(keystone_dungeon_run_id) REFERENCES keystone_dungeon_run(id),
    FOREIGN KEY(character_internal_id) REFERENCES character_info(internal_id),
    FOREIGN KEY(character_spec_id) REFERENCES character_specs(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `keystone_affixes` (
    `id`            INT NOT NULL,
    `name`          VARCHAR(50) NOT NULL,
    `description`   TEXT NOT NULL,
    `icon`          TINYTEXT NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `events` (
    `id`            INT NOT NULL AUTO_INCREMENT,
    `title`         VARCHAR(50) NOT NULL,
    `desc`          TEXT NOT NULL,
    `date`          DATETIME NOT NULL,
    `owner_id`      INT NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `events_asist` (
    `id_asis`   INT NOT NULL AUTO_INCREMENT,
    `id_event`  INT NOT NULL,
    `user_id`   INT NOT NULL,
    PRIMARY KEY(id_asis),
    FOREIGN KEY(id_event) REFERENCES events(id),
    FOREIGN KEY(user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `events_asist_char` (
    `id_asis_char`  INT NOT NULL AUTO_INCREMENT,
    `id_asis`       INT NOT NULL,
    `char_id`       INT NOT NULL,
    `spec_id`       INT NOT NULL
    PRIMARY KEY(id_asis_char),
    FOREIGN KEY(id_asis) REFERENCES events_asist(id_asis),
    FOREIGN KEY(char_id) REFERENCES gMembers_id_name(internal_id),
    FOREIGN KEY(spec_id) REFERENCES character_specs(id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;