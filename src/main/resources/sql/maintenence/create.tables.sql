CREATE TABLE IF NOT EXISTS `${PREFIX}_entity` ( 
  `entityId` int(11) NOT NULL AUTO_INCREMENT, 
  `name` char(16) NOT NULL,  
  `type` enum('player','plugin','group') NOT NULL, 
  PRIMARY KEY (`entityId`)
  ) 
ALTER TABLE `${PREFIX}_entity` ADD UNIQUE KEY `name` (`name`,`type`);
CREATE TABLE IF NOT EXISTS `${PREFIX}_domain`(
  `domainId` int(11) NOT NULL AUTO_INCREMENT, 
  `domain` char(32) NOT NULL,  
  PRIMARY KEY (`domainId`,`domain`)
);
INSERT INTO `${PREFIX}_domain` (`domain`) VALUES ("default");
CREATE TABLE IF NOT EXISTS `${PREFIX}_world`(
  `worldId` int(11) NOT NULL AUTO_INCREMENT, 
  `world` char(32) NOT NULL,  
  PRIMARY KEY (`worldId`,`world`)
);
CREATE TABLE IF NOT EXISTS `${PREFIX}_category`(
  `categoryId` int(11) NOT NULL AUTO_INCREMENT, 
  `category` char(32) NOT NULL,  
  PRIMARY KEY (`categoryId`,`category`)
);
CREATE TABLE IF NOT EXISTS `${PREFIX}_statistic`(
  `statisticId` int(11) NOT NULL AUTO_INCREMENT, 
  `statistic` char(32) NOT NULL,  
  PRIMARY KEY (`statisticId`,`statistic`)
);
-- Create statkeystore
SELECT "Creating value table" as action;
CREATE TABLE IF NOT EXISTS `${PREFIX}_value` (
  `entityId`    int(11) NOT NULL,
  `domainId`    int(11) NOT NULL,
  `worldId`     int(11) NOT NULL,  
  `categoryId`  int(11) NOT NULL,  
  `statisticId` int(11) NOT NULL,  
  `value`       int(11) NOT NULL
  ) 
ENGINE=InnoDB DEFAULT CHARSET=latin1;
ALTER TABLE `${PREFIX}_value` ADD UNIQUE KEY `chkUnique` (`entityId`,`domainId`,`worldId`,`categoryId`,`statisticId`);
ALTER TABLE `${PREFIX}_value` ADD KEY `entityId` (`entityId`);
