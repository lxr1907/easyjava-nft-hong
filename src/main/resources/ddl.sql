CREATE SCHEMA `nft` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin ;

CREATE TABLE `address_invite` (
  `id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(500) COLLATE utf8mb4_bin NOT NULL,
  `address_invite_code` int DEFAULT NULL,
  `email` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

CREATE TABLE `invite_code` (
  `invite_code` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idinvite_code_UNIQUE` (`invite_code`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci