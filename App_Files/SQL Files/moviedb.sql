CREATE SCHEMA `moviedb` ;

CREATE TABLE `moviedb`.`movies` (
  `id` VARCHAR(10) NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `year` INT NOT NULL,
  `director` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `moviedb`.`stars` (
  `id` VARCHAR(10) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `birthYear` INT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `moviedb`.`stars_in_movies` (
  `starId` VARCHAR(10) NOT NULL,
  `movieId` VARCHAR(10) NOT NULL,
  INDEX `starId_idx` (`starId` ASC),
  INDEX `movieId_idx` (`movieId` ASC),
  CONSTRAINT `star_movie_Id`
    FOREIGN KEY (`starId`)
    REFERENCES `moviedb`.`stars` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `movie_star_Id`
    FOREIGN KEY (`movieId`)
    REFERENCES `moviedb`.`movies` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE TABLE `moviedb`.`genres` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `moviedb`.`genres_in_movies` (
  `genreId` INT NOT NULL,
  `movieId` VARCHAR(10) NOT NULL,
  INDEX `genreId_idx` (`genreId` ASC),
  INDEX `movieId_idx` (`movieId` ASC),
  CONSTRAINT `genreId`
    FOREIGN KEY (`genreId`)
    REFERENCES `moviedb`.`genres` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `movieId`
    FOREIGN KEY (`movieId`)
    REFERENCES `moviedb`.`movies` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE TABLE `moviedb`.`creditcards` (
  `id` VARCHAR(20) NOT NULL,
  `firstName` VARCHAR(50) NOT NULL,
  `lastName` VARCHAR(50) NOT NULL,
  `expiration` DATETIME NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `moviedb`.`customers` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(50) NOT NULL,
  `lastName` VARCHAR(50) NOT NULL,
  `ccId` VARCHAR(20) NOT NULL,
  `address` VARCHAR(200) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ccid_idx` (`ccId` ASC),
  CONSTRAINT `ccid`
    FOREIGN KEY (`ccId`)
    REFERENCES `moviedb`.`creditcards` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE TABLE `moviedb`.`sales` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `customerId` INT NOT NULL,
  `movieId` VARCHAR(10) NOT NULL,
  `saleDate` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `customerId_idx` (`customerId` ASC),
  INDEX `movId_idx` (`movieId` ASC),
  CONSTRAINT `customerId`
    FOREIGN KEY (`customerId`)
    REFERENCES `moviedb`.`customers` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `movId`
    FOREIGN KEY (`movieId`)
    REFERENCES `moviedb`.`movies` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE TABLE `moviedb`.`ratings` (
  `movieId` VARCHAR(10) NOT NULL,
  `rating` FLOAT NOT NULL,
  `numVotes` INT NOT NULL,
  INDEX `mov_rate_idx` (`movieId` ASC),
  CONSTRAINT `mov_rate`
    FOREIGN KEY (`movieId`)
    REFERENCES `moviedb`.`movies` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

