CREATE TABLE `playable_races` (
    `id` 	int NOT NULL,
    `mask` 	int NOT NULL,
    `side`	varchar(50) NOT NULL,
    `name`	varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playable_class` (
    `id` 	int NOT NULL,
    `slug`    varchar(50) NOT NULL,
    `name`	varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playable_spec` (
    `id`    INT NOT NULL,
    `slug`  VARCHAR(50) NOT NULL,
    `class` INT NOT NULL,
    `name`  VARCHAR(50) NOT NULL,
    `role`  VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY(`class`) REFERENCES playable_class(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `guild_achievements_list` (
    `id`            INT NOT NULL,
    `title`         TINYTEXT NOT NULL,
    `description`   TINYTEXT NOT NULL,
    `icon`          TINYTEXT NOT NULL,
    `points`        int NOT NULL,
    `classification` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_achievement_category` (
    `id`        INT NOT NULL,
    `name`      VARCHAR(50) NOT NULL,
    `father_id` INT,
    PRIMARY KEY (`id`),
    FOREIGN KEY(father_id) REFERENCES player_achievement_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_achievement_list` (
    `id`            INT NOT NULL,
    `category_id`   INT NOT NULL,
    `title`         TINYTEXT NOT NULL,
    `points`        INT NOT NULL,
    `description`   TINYTEXT NOT NULL,
    `icon`          TINYTEXT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY(category_id) REFERENCES player_achievement_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `spells` (
    `id`            int NOT NULL,
    `name`          TINYTEXT NOT NULL,
    `icon`          varchar(50) NOT NULL,
    `description`   TEXT NOT NULL,
    `castTime`      varchar(20) NOT NULL,
    `cooldown`      varchar(20),
    `range`         varchar(20),
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO spells (`id`,`name`,`icon`,`description`,`castTime`) VALUES (0, "NULL SPELL", "", "", "");

CREATE TABLE `boss_list` (
    `id`            int NOT NULL,
    `name`          varchar(50) NOT NULL,
    `slug`          varchar(50) NOT NULL,
    `description`   TEXT,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `stats` (
    `id`    INT NOT NULL,
    `en_US` VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO stats (`id`, `en_US`) VALUES (3, "Agility");
INSERT INTO stats (`id`, `en_US`) VALUES (4, "Strength");
INSERT INTO stats (`id`, `en_US`) VALUES (5, "Intellect");
INSERT INTO stats (`id`, `en_US`) VALUES (7, "Stamina");
INSERT INTO stats (`id`, `en_US`) VALUES (32, "Critical Strike");
INSERT INTO stats (`id`, `en_US`) VALUES (36, "Haste");
INSERT INTO stats (`id`, `en_US`) VALUES (49, "Mastery");
INSERT INTO stats (`id`, `en_US`) VALUES (40, "Versatility");
INSERT INTO stats (`id`, `en_US`) VALUES (62, "Leech");
INSERT INTO stats (`id`, `en_US`) VALUES (71, "[Agility or Strength or Intellect]");
INSERT INTO stats (`id`, `en_US`) VALUES (72, "[Agility or Strength]");
INSERT INTO stats (`id`, `en_US`) VALUES (74, "[Strength or Intellect]");

CREATE TABLE `enchants` (
    `id`    INT NOT NULL,
    `en_US` VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `items` (
    `id`                    INT NOT NULL,
    `name`                  TINYTEXT NOT NULL,
    `icon`                  TINYTEXT NOT NULL,
    `itemSpell`             INT,
    `gemInfo_bonus_name`    TINYTEXT,
    `gemInfo_type`          VARCHAR(50),
    PRIMARY KEY(id),
    FOREIGN KEY(itemSpell) REFERENCES spells(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO items (`id`, `name`, `icon`) VALUES (0, "NULL ITEM", "NULL ITEM");

CREATE TABLE `raids` (
    `id`            INT NOT NULL AUTO_INCREMENT,
    `slug`          VARCHAR(50) NOT NULL,
    `name`          VARCHAR(50) NOT NULL,
    `total_boss`    TINYINT DEFAULT -1,
    PRIMARY KEY(id),
    UNIQUE(`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `raids` VALUES (1,'the-emerald-nightmare','The Emerald Nightmare',7);
INSERT INTO `raids` VALUES (2,'trial-of-valor','Trial of Valor',3);
INSERT INTO `raids` VALUES (3,'the-nighthold','The Nighthold',10);
INSERT INTO `raids` VALUES (4,'tomb-of-sargeras','Tomb of Sargeras',9);
INSERT INTO `raids` VALUES (5,'antorus-the-burning-throne','Antorus, the Burning Throne',11);
INSERT INTO `raids` VALUES (6,'uldir','Uldir',8);

CREATE TABLE `guild_rank` (
    `id`    INT NOT NULL,
    `title` VARCHAR(20) NOT NULL,
    PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
INSERT INTO `guild_rank` VALUES (0, "Guild Master");
INSERT INTO `guild_rank` VALUES (1, "Officer");