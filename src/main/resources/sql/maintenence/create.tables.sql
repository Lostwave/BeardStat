CREATE TABLE IF NOT EXISTS `${PREFIX}_entity` ( 
  `entityId` int(11) NOT NULL AUTO_INCREMENT, 
  `name` char(32) NOT NULL,  
  `type` char(16) NOT NULL, 
  `scoreboardhide` BOOLEAN NOT NULL DEFAULT false,
  `uuid` CHAR( 32 ) UNIQUE,
  PRIMARY KEY (`entityId`),
  UNIQUE KEY `chkName` (`name`,`type`)
  ) 
ENGINE=InnoDB  DEFAULT CHARSET=latin1;
CREATE TABLE IF NOT EXISTS `${PREFIX}_domain`(
  `domainId` int(11) NOT NULL AUTO_INCREMENT, 
  `domain` char(64) NOT NULL,  
  PRIMARY KEY (`domainId`),
  UNIQUE KEY (`domain`)
);
CREATE TABLE IF NOT EXISTS `${PREFIX}_world`(
  `worldId` int(11) NOT NULL AUTO_INCREMENT, 
  `world` char(64) NOT NULL,  
  `name` char(32) NOT NULL, 
  PRIMARY KEY (`worldId`),
  UNIQUE KEY (`world`)
);
CREATE TABLE IF NOT EXISTS `${PREFIX}_category`(
  `categoryId` int(11) NOT NULL AUTO_INCREMENT, 
  `category` char(64) NOT NULL,  
  `statwrapper` char(32) NOT NULL DEFAULT '%s',
  PRIMARY KEY (`categoryId`),
  UNIQUE KEY (`category`)
);
CREATE TABLE IF NOT EXISTS `${PREFIX}_statistic`(
  `statisticId` int(11) NOT NULL AUTO_INCREMENT, 
  `statistic` char(64) NOT NULL,  
  `name` char(32) NOT NULL, 
  `formatting` ENUM(  'none',  'timestamp',  'time' ) NOT NULL DEFAULT 'none',
  PRIMARY KEY (`statisticId`),
  UNIQUE KEY (`statistic`)
);
CREATE TABLE IF NOT EXISTS `${PREFIX}_value` (
  `entityId`    int(11) NOT NULL,
  `domainId`    int(11) NOT NULL,
  `worldId`     int(11) NOT NULL,  
  `categoryId`  int(11) NOT NULL,  
  `statisticId` int(11) NOT NULL,  
  `value`       int(11) NOT NULL,
  UNIQUE KEY `chkUni` (`entityId`, `domainId`, `worldId`, `categoryId`, `statisticId`),
  KEY `entityId` (`entityId`)
  ) 
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `${PREFIX}_document_meta`(
	`documentId`  int(11)  NOT NULL AUTO_INCREMENT, 
	`entityId`    int(11)  NOT NULL,  
	`domainId`    int(11)  NOT NULL,
	`key`         char(32) NOT NULL, 
	`curRevision` char(40) NULL,
	PRIMARY KEY (`documentId`),
	UNIQUE KEY (`domain`)
)
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `${PREFIX}_document_store`(
        `storeId`     int(11)  NOT NULL AUTO_INCREMENT, 
	`documentId`  int(11)  NOT NULL,
	`revision`    char(40) NOT NULL,
	`parentRev`   char(40),
	`added`       timestamp CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`document`    MEDIUMBLOB,
)
ENGINE=InnoDB DEFAULT CHARSET=latin1;