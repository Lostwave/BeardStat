PRAGMA synchronous = OFF;
PRAGMA journal_mode = MEMORY;
BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `${TBL_ENTITY}` (  `entityId` int(11) NOT NULL AUTO_INCREMENT,  `name` char(16) NOT NULL,  `type` enum('player','plugin','group') NOT NULL, PRIMARY KEY (`entityId`),  UNIQUE KEY `name` (`name`,`type`)) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
CREATE TABLE IF NOT EXISTS `${TBL_KEYSTORE}` (  `entityId` int(11) NOT NULL,  `domain` char(32) NOT NULL,  `world` char(32) NOT NULL,  `category` char(32) NOT NULL,  `statistic` char(32) NOT NULL,  `value` int(11) NOT NULL,  UNIQUE KEY `chkUni` (`entityId`,`domain`,`world`,`category`,`statistic`),  KEY `entityId` (`entityId`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;
END TRANSACTION;
