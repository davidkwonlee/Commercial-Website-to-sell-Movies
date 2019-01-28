CREATE TABLE `moviedb`.`employees` (
  `email` VARCHAR(50) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  `fullname` VARCHAR(100) NULL,
  PRIMARY KEY (`email`));
  
  INSERT INTO `moviedb`.`employees` (`email`, `password`, `fullname`) VALUES ('classta@email.edu', 'classta', 'TA CS122B');
