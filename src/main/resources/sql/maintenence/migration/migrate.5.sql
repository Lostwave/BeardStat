#Migrating to yggdrasil 1.2;
#Adding uuid field;
ALTER TABLE  `${PREFIX}_entity` ADD `uuid` CHAR( 32 );
#Done migrating;