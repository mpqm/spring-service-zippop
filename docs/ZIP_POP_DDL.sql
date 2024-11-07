-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema test
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema test
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `test` DEFAULT CHARACTER SET latin1 ;
-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

USE `test` ;

-- -----------------------------------------------------
-- Table `test`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`customer` (
  `customer_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `customer_id` VARCHAR(45) NOT NULL,
  `customer_name` VARCHAR(45) NOT NULL,
  `customer_passwd` VARCHAR(45) NOT NULL,
  `customer_addr` VARCHAR(45) NULL DEFAULT NULL,
  `customer_phone` VARCHAR(45) NULL DEFAULT NULL,
  `customer_e_mail` VARCHAR(45) NULL DEFAULT NULL,
  `customer_point` INT(11) NULL DEFAULT NULL,
  `cancel_waiting_num` INT(11) NOT NULL,
  PRIMARY KEY (`customer_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`cancel_waiting_mem`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`cancel_waiting_mem` (
  `cancel_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `customer_idx` INT(11) NOT NULL,
  `store_idx` INT(11) NOT NULL,
  PRIMARY KEY (`cancel_idx`),
    FOREIGN KEY (`customer_idx`)
    REFERENCES `test`.`customer` (`customer_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`company` (
  `company_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `company_id` VARCHAR(45) NOT NULL,
  `company_name` VARCHAR(45) NOT NULL,
  `company_e_mail` VARCHAR(45) NOT NULL,
  `company_passwd` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`company_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`popup_store`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`popup_store` (
  `store_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `store_name` VARCHAR(45) NOT NULL,
  `store_addr` VARCHAR(45) NOT NULL,
  `store_content` TEXT NULL DEFAULT NULL,
  `store_date` DATETIME NOT NULL,
  `category` VARCHAR(45) NOT NULL,
  `company_idx` INT(11) NOT NULL,
  `rating` INT(11) NULL DEFAULT NULL,
  `store_image` TEXT NULL DEFAULT NULL,
  `total_people` INT(11) NOT NULL,
  PRIMARY KEY (`store_idx`),
    FOREIGN KEY (`company_idx`)
    REFERENCES `test`.`company` (`company_idx`),
    FOREIGN KEY (`company_idx`)
    REFERENCES `test`.`company` (`company_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`popup_goods`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`popup_goods` (
  `product_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(45) NOT NULL,
  `product_price` INT(11) NOT NULL,
  `product_content` TEXT NULL DEFAULT NULL,
  `product_image` TEXT NULL DEFAULT NULL,
  `product_amount` INT(11) NOT NULL,
  `store_idx` INT(11) NOT NULL,
  PRIMARY KEY (`product_idx`),
    FOREIGN KEY (`store_idx`)
    REFERENCES `test`.`popup_store` (`store_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`cart`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`cart` (
  `cart_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `product_idx` INT(11) NULL DEFAULT NULL,
  `amount` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`cart_idx`),
    FOREIGN KEY (`product_idx`)
    REFERENCES `test`.`popup_goods` (`product_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`cart_goods`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`cart_goods` (
  `cart_goods_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `product_idx` INT(11) NOT NULL,
  `cart_idx` INT(11) NOT NULL,
  `customer_idx` INT(11) NOT NULL,
  PRIMARY KEY (`cart_goods_idx`),
    FOREIGN KEY (`cart_idx`)
    REFERENCES `test`.`cart` (`cart_idx`),
    FOREIGN KEY (`customer_idx`)
    REFERENCES `test`.`customer` (`customer_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`posts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`posts` (
  `post_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `post_title` VARCHAR(45) NOT NULL,
  `customer_idx` INT(11) NOT NULL,
  `post_content` TEXT NOT NULL,
  PRIMARY KEY (`post_idx`),
    FOREIGN KEY (`customer_idx`)
    REFERENCES `test`.`customer` (`customer_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`comment` (
  `comment_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `customer_idx` INT(11) NOT NULL,
  `post_idx` INT(11) NOT NULL,
  `comment_content` TEXT NOT NULL,
  PRIMARY KEY (`comment_idx`),
    FOREIGN KEY (`customer_idx`)
    REFERENCES `test`.`customer` (`customer_idx`),
    FOREIGN KEY (`post_idx`)
    REFERENCES `test`.`posts` (`post_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`popup_customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`popup_customer` (
  `popup_customer_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `customer_idx` INT(11) NOT NULL,
  `store_idx` INT(11) NOT NULL,
  `reservation_datetime` DATETIME NOT NULL,
  PRIMARY KEY (`popup_customer_idx`),
    FOREIGN KEY (`customer_idx`)
    REFERENCES `test`.`customer` (`customer_idx`),
    FOREIGN KEY (`store_idx`)
    REFERENCES `test`.`popup_store` (`store_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`popup_customer_goods`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`popup_customer_goods` (
  `popup_customer_goods_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `popup_customer_idx` INT(11) NOT NULL,
  `product_idx` INT(11) NOT NULL,
  PRIMARY KEY (`popup_customer_goods_idx`),
    FOREIGN KEY (`popup_customer_idx`)
    REFERENCES `test`.`popup_customer` (`popup_customer_idx`),
    FOREIGN KEY (`product_idx`)
    REFERENCES `test`.`popup_goods` (`product_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`payment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`payment` (
  `pay_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `customer_idx` INT(11) NOT NULL,
  `pay_datetime` DATETIME NOT NULL,
  `pay_total` INT(11) NOT NULL,
  `popup_customer_goods_idx` INT(11) NOT NULL,
  PRIMARY KEY (`pay_idx`),
    FOREIGN KEY (`customer_idx`)
    REFERENCES `test`.`customer` (`customer_idx`),
    FOREIGN KEY (`popup_customer_goods_idx`)
    REFERENCES `test`.`popup_customer_goods` (`popup_customer_goods_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`review` (
  `review_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `review_title` VARCHAR(45) NOT NULL,
  `review_content` TEXT NULL DEFAULT NULL,
  `rating` INT(11) NOT NULL,
  `review_datetime` DATETIME NULL DEFAULT NULL,
  `popup_customer_idx` INT(11) NOT NULL,
  PRIMARY KEY (`review_idx`),
    FOREIGN KEY (`popup_customer_idx`)
    REFERENCES `test`.`popup_customer` (`popup_customer_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`wishlist`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`wishlist` (
  `wish_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `store_idx` INT(11) NOT NULL,
  PRIMARY KEY (`wish_idx`),
    FOREIGN KEY (`store_idx`)
    REFERENCES `test`.`popup_store` (`store_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `test`.`wish_popup`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`wish_popup` (
  `wish_popup_idx` INT(11) NOT NULL AUTO_INCREMENT,
  `wish_idx` INT(11) NOT NULL,
  `store_idx` INT(11) NOT NULL,
  `customer_idx` INT(11) NOT NULL,
  PRIMARY KEY (`wish_popup_idx`),
    FOREIGN KEY (`wish_idx`)
    REFERENCES `test`.`wishlist` (`wish_idx`),
    FOREIGN KEY (`store_idx`)
    REFERENCES `test`.`popup_store` (`store_idx`),
    FOREIGN KEY (`customer_idx`)
    REFERENCES `test`.`customer` (`customer_idx`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
