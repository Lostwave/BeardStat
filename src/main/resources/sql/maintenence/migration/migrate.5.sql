#Migrating to yggdrasil 1.2;
#Adding String field to entities
ALTER TABLE  `${PREFIX}_entity` ADD `uuid` CHAR( 32 );
#Done migrating