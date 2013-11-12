#Migrating to yggdrasil 1.3;
#Repatching type field to be char field
ALTER TABLE  `${PREFIX}_entity` CHANGE  `type` `type` CHAR( 16 ) NOT NULL DEFAULT  'player';
#pre-caching player uuids;