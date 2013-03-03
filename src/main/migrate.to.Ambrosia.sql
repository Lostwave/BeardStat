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

CREATE TABLE IF NOT EXISTS `stat_entity` ( 
  `entityId` int(11) NOT NULL AUTO_INCREMENT, 
  `name` char(16) NOT NULL,  
  `type` enum('player','plugin','group') NOT NULL, 
  PRIMARY KEY (`entityId`)
  ) 
ENGINE=InnoDB  DEFAULT CHARSET=latin1;


-- Populate table with players
SELECT "Populating entity table" as action;
INSERT INTO `stat_entity` (`name`,`type`) SELECT `player`,"player" as `type` FROM `stats` GROUP BY `player`;

SELECT "Indexing entity table" as action;
ALTER TABLE `stat_entity` ADD UNIQUE KEY `name` (`name`,`type`);

-- Create statkeystore
SELECT "Creating keystore table" as action;
CREATE TABLE IF NOT EXISTS `stat_keystore` (
  `entityId` int(11) NOT NULL,
  `domain` char(32) NOT NULL,
  `world` char(32) NOT NULL,  
  `category` char(32) NOT NULL,  
  `statistic` char(32) NOT NULL,  
  `value` int(11) NOT NULL
  ) 
ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Populate with data
SELECT "Populating keystore table" as action;
INSERT into `stat_keystore`
SELECT  
`entityId` , 
"default" AS  `domain` , 
 "__imported__" AS  `world` ,  
 `category` ,  
 `stat` ,  
 `value` 
FROM  
`stats` ,  
`stat_entity` 
WHERE  
`player` =  `name` AND  
`type` =  'player';

-- Re-initialise indexes
SELECT "Indexing keystore table (WARNING: MAY TAKE A WHILE" as action;
 ALTER TABLE `stat_keystore` ADD UNIQUE KEY `chkUni` (`entityId`,`domain`,`world`,`category`,`statistic`);
 ALTER TABLE `stat_keystore` ADD KEY `entityId` (`entityId`);
 SELECT "Finished!" as action;