CREATE TABLE `races` (
    `id` 	int NOT NULL,
    `mask` 	int NOT NULL,
    `side`	varchar(50) NOT NULL,
    `name`	varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playable_class` (
    `id` 	int NOT NULL,
    `en_US`	varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `guild_achievements_list` (
    `id`            INT NOT NULL,
    `title`         TINYTEXT NOT NULL,
    `description`   TINYTEXT NOT NULL,
    `icon`          VARCHAR(50) NOT NULL,
    `points`        int NOT NULL,
    `classification` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_achivement_category` (
    `id`        INT NOT NULL,
    `name`      VARCHAR(20) NOT NULL,
    `father_id` INT,
    PRIMARY KEY (`id`),
    FOREIGN KEY(father_id) REFERENCES player_achivement_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_achivement_list` (
    `id`            INT NOT NULL,
    `category_id`   INT NOT NULL,
    `title`         TINYTEXT NOT NULL,
    `points`        INT NOT NULL,
    `description`   TINYTEXT NOT NULL,
    `icon` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY(category_id) REFERENCES player_achivement_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `spells` (
    `id`            int NOT NULL,
    `name`          varchar(60) NOT NULL,
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
INSERT INTO stats (`id`, `en_US`) VALUES (71, "Agility or Strength or Intellect");
INSERT INTO stats (`id`, `en_US`) VALUES (72, "Agility or Strength");
INSERT INTO stats (`id`, `en_US`) VALUES (74, "Strength or Intellect");

CREATE TABLE `items` (
    `id`    INT NOT NULL,
    `name`  VARCHAR(50) NOT NULL,
    `icon`  TINYTEXT NOT NULL,
    `itemSpell`            INT,
    `gemInfo_bonus_name`    VARCHAR(50),
    `gemInfo_type`          VARCHAR(50),
    PRIMARY KEY(id),
    FOREIGN KEY(itemSpells) REFERENCES spells(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO items (`id`, `name`, `icon`) VALUES (0, "NULL ITEM", "NULL ITEM");