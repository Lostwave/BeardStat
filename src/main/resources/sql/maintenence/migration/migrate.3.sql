#Migrating to yggdrasil 1.1;
#Adding metadata tables to statistics
ALTER TABLE  `${PREFIX}_statistic` ADD  `name` CHAR( 32 );
ALTER TABLE  `${PREFIX}_statistic` ADD  `formatting` ENUM(  'none',  'timestamp',  'time' ) NOT NULL
#