CREATE TABLE `users` (
    `email`         varchar(50) NOT NULL,
    `password`      varchar(50) NOT NULL,
    `battle_tag`    varchar(50),
    `access_code`   varchar(50),
    PRIMARY KEY(email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;