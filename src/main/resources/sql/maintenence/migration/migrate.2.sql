CREATE TABLE IF NOT EXISTS `${PREFIX}_entity` ( 
  `entityId` int(11) NOT NULL AUTO_INCREMENT, 
  `name` char(16) NOT NULL,  
  `type` enum('player','plugin','group') NOT NULL, 
  PRIMARY KEY (`entityId`)
  ) 
ENGINE=InnoDB  DEFAULT CHARSET=latin1;

INSERT INTO `${PREFIX}_entity` (`name`,`type`) SELECT `player`,"player" as `type` FROM `${OLD_TBL}` GROUP BY `player`;
ALTER TABLE `${PREFIX}_entity` ADD UNIQUE KEY `name` (`name`,`type`);

CREATE TABLE IF NOT EXISTS `${PREFIX}_domain`(
  `domainId` int(11) NOT NULL AUTO_INCREMENT, 
  `domain` char(32) NOT NULL,  
  PRIMARY KEY (`domainId`,`domain`)
);
INSERT INTO `${PREFIX}_domain` (`domain`) VALUES ("default");
SET @domainId := (SELECT `domainId` from `${PREFIX}_domain` WHERE `domain` = "default");

CREATE TABLE IF NOT EXISTS `${PREFIX}_world`(
  `worldId` int(11) NOT NULL AUTO_INCREMENT, 
  `world` char(32) NOT NULL,  
  PRIMARY KEY (`worldId`,`world`)
);
INSERT INTO `${PREFIX}_world` (`world`) VALUES ("__imported__");
SET @worldId := (SELECT `worldId` from `${PREFIX}_world` WHERE `world` = "__imported__");


CREATE TABLE IF NOT EXISTS `${PREFIX}_category`(
  `categoryId` int(11) NOT NULL AUTO_INCREMENT, 
  `category` char(32) NOT NULL,  
  PRIMARY KEY (`categoryId`,`category`)
);
INSERT INTO `${PREFIX}_category` (`category`) SELECT DISTINCT(`category`) from stats;

CREATE TABLE IF NOT EXISTS `${PREFIX}_statistic`(
  `statisticId` int(11) NOT NULL AUTO_INCREMENT, 
  `statistic` char(32) NOT NULL,  
  PRIMARY KEY (`statisticId`,`statistic`)
);
INSERT INTO `${PREFIX}_statistic` (`statistic`) SELECT DISTINCT(`stat`) from stats;

CREATE TABLE IF NOT EXISTS `${PREFIX}_value` (
  `entityId`    int(11) NOT NULL,
  `domainId`    int(11) NOT NULL,
  `worldId`     int(11) NOT NULL,  
  `categoryId`  int(11) NOT NULL,  
  `statisticId` int(11) NOT NULL,  
  `value`       int(11) NOT NULL
  ) 
ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

ALTER TABLE `${PREFIX}_value` ADD UNIQUE KEY `chkUni` (`entityId`,`domainId`,`worldId`,`categoryId`,`statisticId`);
ALTER TABLE `${PREFIX}_value` ADD KEY `entityId` (`entityId`);
SELECT "Finished!" as action;
