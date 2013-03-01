CREATE TABLE IF NOT EXISTS `${TBL_ENTITY}` (  
  `entityId` INTEGER PRIMARY KEY AUTOINCREMENT, 
   `name` CHARACTER(16) NOT NULL,  
   `type` CHARACTER(16) NOT NULL
   );
CREATE UNIQUE INDEX `name` (`name`,`type`);

CREATE TABLE IF NOT EXISTS `${TBL_KEYSTORE}` (  
  `entityId` INTEGER, 
   `domain` CHARACTER(32) NOT NULL,  
  `world` CHARACTER(32) NOT NULL,  
  `category` CHARACTER(32) NOT NULL,  
  `statistic` CHARACTER(32) NOT NULL,  
  `value` INTEGER
  );
CREATE UNIQUE INDEX  `chkUni` (`entityId`,`domain`,`world`,`category`,`statistic`);
CREATE INDEX `entityId` (`entityId`);