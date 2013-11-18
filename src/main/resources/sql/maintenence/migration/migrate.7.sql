#Migrating to yggdrasil 1.4;
#Adding document tables;
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