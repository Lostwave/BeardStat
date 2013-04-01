#Migrating to yggdrasil format;
#Creating entity table;
CREATE TABLE IF NOT EXISTS `${PREFIX}_entity` ( 
  `entityId` int(11) NOT NULL AUTO_INCREMENT, 
  `name` char(32) NOT NULL,  
  `type` enum('player','plugin','group') NOT NULL, 
  PRIMARY KEY (`entityId`),
  UNIQUE KEY (`name`,`type`)
  ) 
ENGINE=InnoDB  DEFAULT CHARSET=latin1;
#Populating entity table;
INSERT INTO `${PREFIX}_entity` (`name`,`type`) SELECT `player`,"player" as `type` FROM `${OLD_TBL}` GROUP BY `player`;
#Indexing entity key;
ALTER TABLE `${PREFIX}_entity` ADD UNIQUE KEY `name` (`name`,`type`);
#Creating domain table and initialising default domain;
CREATE TABLE IF NOT EXISTS `${PREFIX}_domain`(
  `domainId` int(11) NOT NULL AUTO_INCREMENT, 
  `domain` char(32) NOT NULL,  
  PRIMARY KEY (`domainId`)
  UNIQUE KEY (`domain`)
);
INSERT INTO `${PREFIX}_domain` (`domain`) VALUES ("default");
SET @domainId := (SELECT `domainId` from `${PREFIX}_domain` WHERE `domain` = "default");
#Creating world table;
CREATE TABLE IF NOT EXISTS `${PREFIX}_world`(
  `worldId` int(11) NOT NULL AUTO_INCREMENT, 
  `world` char(32) NOT NULL,  
  PRIMARY KEY (`worldId`),
  UNIQUE KEY (`world`)
);
#Creating __imported__ world for stat migration;
INSERT INTO `${PREFIX}_world` (`world`) VALUES ("__imported__");
SET @worldId := (SELECT `worldId` from `${PREFIX}_world` WHERE `world` = "__imported__");
#Creating category table;
CREATE TABLE IF NOT EXISTS `${PREFIX}_category`(
  `categoryId` int(11) NOT NULL AUTO_INCREMENT, 
  `category` char(32) NOT NULL,  
  PRIMARY KEY (`categoryId`),
  UNIQUE KEY (`category`)
);
#Populating category table;
INSERT INTO `${PREFIX}_category` (`category`) SELECT DISTINCT(`category`) from stats;
#Creating Statistic table;
CREATE TABLE IF NOT EXISTS `${PREFIX}_statistic`(
  `statisticId` int(11) NOT NULL AUTO_INCREMENT, 
  `statistic` char(32) NOT NULL,  
  PRIMARY KEY (`statisticId`),
  UNIQUE KEY (`statistic`)
);
#Populating statistic table;
INSERT INTO `${PREFIX}_statistic` (`statistic`) SELECT DISTINCT(`stat`) from stats;
#Creating value table;
CREATE TABLE IF NOT EXISTS `${PREFIX}_value` (
  `entityId`    int(11) NOT NULL,
  `domainId`    int(11) NOT NULL,
  `worldId`     int(11) NOT NULL,  
  `categoryId`  int(11) NOT NULL,  
  `statisticId` int(11) NOT NULL,  
  `value`       int(11) NOT NULL,
  UNIQUE KEY (`entityId`, `domainId`, `worldId`, `categoryId`, `statisticId`)
  ) 
ENGINE=InnoDB DEFAULT CHARSET=latin1;
#Populating value table, WARNING: THIS WILL TAKE A LONG TIME WITH LARGE DATABASES;
INSERT into `${PREFIX}_value`
SELECT  
`entityId` , 
@domainId as `domain` , 
@worldId  as `world` ,  
`categoryId` ,  
`statisticId` ,  
 `value` 
FROM  
`${OLD_TBL}` ,  
`${PREFIX}_entity`,
`${PREFIX}_category`,
`${PREFIX}_statistic`

WHERE  
`player` = `name` AND 
`type` =  'player' AND
`${PREFIX}_category`.`category` = `${OLD_TBL}`.`category` AND
`${PREFIX}_statistic`.`statistic` = `stat`
;
#Reindexing value table, this may take a while;
ALTER TABLE `${PREFIX}_value` ADD UNIQUE KEY `chkUni` (`entityId`,`domainId`,`worldId`,`categoryId`,`statisticId`);
ALTER TABLE `${PREFIX}_value` ADD KEY `entityId` (`entityId`);
#Migration to yggdrasil format complete;