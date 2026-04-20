-- zippop.cart definition

CREATE TABLE `cart` (
  `created_at` datetime(6) NOT NULL,
  `customer_idx` bigint(20) DEFAULT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_idx` bigint(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKl34sey1nr30ake42gp4kdl4kh` (`customer_idx`),
  CONSTRAINT `FKl34sey1nr30ake42gp4kdl4kh` FOREIGN KEY (`customer_idx`) REFERENCES `customer` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- zippop.cart_item definition
CREATE TABLE `cart_item` (
  `count` int(11) NOT NULL CHECK (`count` >= 1),
  `price` int(11) NOT NULL,
  `cart_idx` bigint(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `goods_idx` bigint(20) DEFAULT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`idx`),
  KEY `FK1h1rbspot8el8u6p3emwfjuve` (`cart_idx`),
  CONSTRAINT `FK1h1rbspot8el8u6p3emwfjuve` FOREIGN KEY (`cart_idx`) REFERENCES `cart` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.company definition

CREATE TABLE `company` (
  `is_email_auth` bit(1) NOT NULL,
  `is_in_active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `crn` varchar(15) DEFAULT NULL,
  `phone_number` varchar(15) NOT NULL,
  `user_id` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `address` varchar(200) NOT NULL,
  `password` varchar(255) NOT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `role` enum('COMPLETE','GOODS_RESERVED','GOODS_STOCK','INCOMPLETE','RESERVE_CANCEL','RESERVE_COMPLETE','RESERVE_DELIVERY','RESERVE_READY','ROLE_COMPANY','ROLE_CUSTOMER','STOCK_CANCEL','STOCK_COMPLETE','STOCK_DELIVERY','STOCK_READY','STORE_END','STORE_START') NOT NULL,
  PRIMARY KEY (`idx`),
  UNIQUE KEY `UKg82ixrst2tc542u5s214ggpdf` (`user_id`),
  UNIQUE KEY `UKbma9lv19ba3yjwf12a34xord3` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.customer definition

CREATE TABLE `customer` (
  `is_email_auth` bit(1) NOT NULL,
  `is_in_active` bit(1) NOT NULL,
  `point` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `phone_number` varchar(15) NOT NULL,
  `user_id` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `address` varchar(200) NOT NULL,
  `password` varchar(255) NOT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `role` enum('COMPLETE','GOODS_RESERVED','GOODS_STOCK','INCOMPLETE','RESERVE_CANCEL','RESERVE_COMPLETE','RESERVE_DELIVERY','RESERVE_READY','ROLE_COMPANY','ROLE_CUSTOMER','STOCK_CANCEL','STOCK_COMPLETE','STOCK_DELIVERY','STOCK_READY','STORE_END','STORE_START') NOT NULL,
  PRIMARY KEY (`idx`),
  UNIQUE KEY `UKj7ja2xvrxudhvssosd4nu1o92` (`user_id`),
  UNIQUE KEY `UKdwk6cx0afu8bs9o4t536v1j5v` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.goods definition

CREATE TABLE `goods` (
  `amount` int(11) NOT NULL CHECK (`amount` >= 0),
  `price` int(11) NOT NULL CHECK (`price` >= 1),
  `created_at` datetime(6) NOT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_idx` bigint(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `name` varchar(1000) NOT NULL,
  `status` enum('COMPLETE','GOODS_RESERVED','GOODS_STOCK','INCOMPLETE','RESERVE_CANCEL','RESERVE_COMPLETE','RESERVE_DELIVERY','RESERVE_READY','ROLE_COMPANY','ROLE_CUSTOMER','STOCK_CANCEL','STOCK_COMPLETE','STOCK_DELIVERY','STOCK_READY','STORE_END','STORE_START') NOT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKgerlk1ko4b2a8h7618awfu0xx` (`store_idx`),
  CONSTRAINT `FKgerlk1ko4b2a8h7618awfu0xx` FOREIGN KEY (`store_idx`) REFERENCES `store` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.goods_image definition

CREATE TABLE `goods_image` (
  `created_at` datetime(6) NOT NULL,
  `goods_idx` bigint(20) DEFAULT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idx`),
  KEY `FK4bucjp6cvk5wi2x0f9r9sujwn` (`goods_idx`),
  CONSTRAINT `FK4bucjp6cvk5wi2x0f9r9sujwn` FOREIGN KEY (`goods_idx`) REFERENCES `goods` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- zippop.orders definition

CREATE TABLE `orders` (
  `delivery_cost` int(11) NOT NULL,
  `total_price` int(11) NOT NULL,
  `used_point` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_idx` bigint(20) DEFAULT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_idx` bigint(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `imp_uid` varchar(100) NOT NULL,
  `status` enum('COMPLETE','GOODS_RESERVED','GOODS_STOCK','INCOMPLETE','RESERVE_CANCEL','RESERVE_COMPLETE','RESERVE_DELIVERY','RESERVE_READY','ROLE_COMPANY','ROLE_CUSTOMER','STOCK_CANCEL','STOCK_COMPLETE','STOCK_DELIVERY','STOCK_READY','STORE_END','STORE_START') NOT NULL,
  PRIMARY KEY (`idx`),
  UNIQUE KEY `UKp7elu6573h8plsddijae5xf74` (`imp_uid`),
  KEY `FK7g7gqx9dsfjb1tj9015d1gvis` (`customer_idx`),
  CONSTRAINT `FK7g7gqx9dsfjb1tj9015d1gvis` FOREIGN KEY (`customer_idx`) REFERENCES `customer` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.orders_detail definition

CREATE TABLE `orders_detail` (
  `each_price` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `goods_idx` bigint(20) DEFAULT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `orders_idx` bigint(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKpska5n1lay6xya92gd9hmnkb4` (`goods_idx`),
  CONSTRAINT `FKpska5n1lay6xya92gd9hmnkb4` FOREIGN KEY (`goods_idx`) REFERENCES `goods` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.payout definition

CREATE TABLE `payout` (
  `payout_date` date NOT NULL,
  `total_revenue` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_idx` bigint(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETE','GOODS_RESERVED','GOODS_STOCK','INCOMPLETE','RESERVE_CANCEL','RESERVE_COMPLETE','RESERVE_DELIVERY','RESERVE_READY','ROLE_COMPANY','ROLE_CUSTOMER','STOCK_CANCEL','STOCK_COMPLETE','STOCK_DELIVERY','STOCK_READY','STORE_END','STORE_START') NOT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKbywvc1a1vx2uquar6ts66b8uk` (`store_idx`),
  CONSTRAINT `FKbywvc1a1vx2uquar6ts66b8uk` FOREIGN KEY (`store_idx`) REFERENCES `store` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- zippop.reserve definition

CREATE TABLE `reserve` (
  `start_date` date NOT NULL,
  `total_people` int(11) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_time` datetime(6) NOT NULL,
  `store_idx` bigint(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `waitinguuid` varchar(36) NOT NULL,
  `workinguuid` varchar(36) NOT NULL,
  PRIMARY KEY (`idx`),
  KEY `FK8u47sxt09tbrtq315f5nlvwpr` (`store_idx`),
  CONSTRAINT `FK8u47sxt09tbrtq315f5nlvwpr` FOREIGN KEY (`store_idx`) REFERENCES `store` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.store definition

CREATE TABLE `store` (
  `like_count` int(11) NOT NULL,
  `total_people` int(11) NOT NULL,
  `company_idx` bigint(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `end_date` varchar(10) NOT NULL,
  `start_date` varchar(10) NOT NULL,
  `category` varchar(50) NOT NULL,
  `company_email` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(200) NOT NULL,
  `content` varchar(500) NOT NULL,
  `status` enum('COMPLETE','GOODS_RESERVED','GOODS_STOCK','INCOMPLETE','RESERVE_CANCEL','RESERVE_COMPLETE','RESERVE_DELIVERY','RESERVE_READY','ROLE_COMPANY','ROLE_CUSTOMER','STOCK_CANCEL','STOCK_COMPLETE','STOCK_DELIVERY','STOCK_READY','STORE_END','STORE_START') NOT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKjcsdqh8rhbgg12edp2bjoulbn` (`company_idx`),
  CONSTRAINT `FKjcsdqh8rhbgg12edp2bjoulbn` FOREIGN KEY (`company_idx`) REFERENCES `company` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- zippop.store_image definition

CREATE TABLE `store_image` (
  `created_at` datetime(6) NOT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_idx` bigint(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKjr3g8phvjfnu1lkvfpcnxx47` (`store_idx`),
  CONSTRAINT `FKjr3g8phvjfnu1lkvfpcnxx47` FOREIGN KEY (`store_idx`) REFERENCES `store` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- zippop.store_like definition

CREATE TABLE `store_like` (
  `created_at` datetime(6) NOT NULL,
  `customer_idx` bigint(20) DEFAULT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_idx` bigint(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKm71uj1j6edcoose47ha0v2qqr` (`customer_idx`),
  CONSTRAINT `FKm71uj1j6edcoose47ha0v2qqr` FOREIGN KEY (`customer_idx`) REFERENCES `customer` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- zippop.store_review definition

CREATE TABLE `store_review` (
  `rating` int(11) NOT NULL CHECK (`rating` >= 1 and `rating` <= 5),
  `created_at` datetime(6) NOT NULL,
  `customer_idx` bigint(20) DEFAULT NULL,
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_idx` bigint(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_name` varchar(50) NOT NULL,
  `customer_email` varchar(100) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` varchar(1000) NOT NULL,
  PRIMARY KEY (`idx`),
  KEY `FKewv5xsqjvi7m2mpovk5f3y9xs` (`customer_idx`),
  CONSTRAINT `FKewv5xsqjvi7m2mpovk5f3y9xs` FOREIGN KEY (`customer_idx`) REFERENCES `customer` (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;