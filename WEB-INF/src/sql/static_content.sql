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
    `id`            int NOT NULL,
    `title`         VARCHAR(50) NOT NULL,
    `description`   VARCHAR(150) NOT NULL,
    `icon`          VARCHAR(50) NOT NULL,
    `points`        int NOT NULL,
    `classification` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;