#Migrating to yggdrasil 1.1;
#Adding metadata tables to statistics;
ALTER TABLE  `${PREFIX}_statistic` ADD  `name` CHAR( 32 );
ALTER TABLE  `${PREFIX}_statistic` ADD  `formatting` ENUM(  'none',  'timestamp',  'time' ) NOT NULL;
ALTER TABLE  `${PREFIX}_entity`    ADD  `scoreboardhide` BOOLEAN NOT NULL;
ALTER TABLE  `${PREFIX}_category`  ADD  `statwrapper` CHAR(64);
ALTER TABLE  `${PREFIX}_entity` CHANGE  `type`  `type` CHAR( 16 ) NOT NULL DEFAULT  'player';
#Done migrating