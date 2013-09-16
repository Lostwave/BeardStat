#Migrating to yggdrasil 1.2;
#Increasing key space
ALTER TABLE  `${PREFIX}_statistic` MODIFY `statistic` CHAR( 64 );
ALTER TABLE  `${PREFIX}_world`     MODIFY `world`     CHAR( 64 );
ALTER TABLE  `${PREFIX}_category`  MODIFY `category`  CHAR( 64 );
ALTER TABLE  `${PREFIX}_domain`    MODIFY `domain`    CHAR( 64 );
#Done migrating