-- 
-- Base to Ambrosia conversion script
-- 
-- Currently stats are stored as a single table, this is BAD
-- 
-- Amborsia improves the storage mechanism
-- 
-- player -> entity
-- new entity types (plugin, group alongside player)
-- Future : UUID -> entity when mojang does that.
-- Seperation of entity from stats
-- Addition of domain (Context for stat) and world
-- 
-- 
-- 

-- Create entity table
SELECT "Creating entity table" as action;

CREATE TABLE IF NOT EXISTS `stats_entity` ( 
  `entityId` int(11) NOT NULL AUTO_INCREMENT, 
  `name` char(16) NOT NULL,  
  `type` enum('player','plugin','group') NOT NULL, 
  PRIMARY KEY (`entityId`)
  ) 
ENGINE=InnoDB  DEFAULT CHARSET=latin1;


-- Populate table with players
SELECT "Populating entity table" as action;
INSERT INTO `stats_entity` (`name`,`type`) SELECT `player`,"player" as `type` FROM `stats` GROUP BY `player`;

SELECT "Indexing entity table" as action;
ALTER TABLE `stats_entity` ADD UNIQUE KEY `name` (`name`,`type`);

CREATE TABLE IF NOT EXISTS `stats_domain`(
  `domainId` int(11) NOT NULL AUTO_INCREMENT, 
  `domain` char(32) NOT NULL,  
  PRIMARY KEY (`domainId`,`domain`)
);
INSERT INTO `stats_domain` (`domain`) VALUES ("default");
SET @domainId := (SELECT `domainId` from `stats_domain` WHERE `domain` = "default");

CREATE TABLE IF NOT EXISTS `stats_world`(
  `worldId` int(11) NOT NULL AUTO_INCREMENT, 
  `world` char(32) NOT NULL,  
  PRIMARY KEY (`worldId`,`world`)
);
INSERT INTO `stats_world` (`world`) VALUES ("__imported__");
SET @worldId := (SELECT `worldId` from `stats_world` WHERE `world` = "__imported__");


CREATE TABLE IF NOT EXISTS `stats_category`(
  `categoryId` int(11) NOT NULL AUTO_INCREMENT, 
  `category` char(32) NOT NULL,  
  PRIMARY KEY (`categoryId`,`category`)
);
INSERT INTO `stats_category` (`category`) SELECT DISTINCT(`category`) from stats;

CREATE TABLE IF NOT EXISTS `stats_statistic`(
  `statisticId` int(11) NOT NULL AUTO_INCREMENT, 
  `statistic` char(32) NOT NULL,  
  PRIMARY KEY (`statisticId`,`statistic`)
);
INSERT INTO `stats_statistic` (`statistic`) SELECT DISTINCT(`stat`) from stats;

-- Create statkeystore
SELECT "Creating keystore table" as action;
CREATE TABLE IF NOT EXISTS `stats_keystore` (
  `entityId`    int(11) NOT NULL,
  `domainId`    int(11) NOT NULL,
  `worldId`     int(11) NOT NULL,  
  `categoryId`  int(11) NOT NULL,  
  `statisticId` int(11) NOT NULL,  
  `value`       int(11) NOT NULL
  ) 
ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Populate with data
SELECT "Populating keystore table" as action;
INSERT into `stats_keystore`
SELECT  
`entityId` , 
@domainId as `domain` , 
@worldId  as `world` ,  
`categoryId` ,  
`statId` ,  
 `value` 
FROM  
`stats` ,  
`stats_entity`,
`stats_category`,
`stats_statistic`

WHERE  
`player` = `name` AND 
`type` =  'player' AND
`stats_category`.`category` = `stats`.`category` AND
`stats_statistic`.`statistic` = `stat`
;

-- Re-initialise indexes
#SELECT "Indexing keystore table (WARNING: MAY TAKE A WHILE)" as action;
#ALTER TABLE `stats_keystore` ADD UNIQUE KEY `chkUni` (`entityId`,`domain`,`world`,`category`,`statistic`);
#ALTER TABLE `stats_keystore` ADD KEY `entityId` (`entityId`);
#SELECT "Finished!" as action;