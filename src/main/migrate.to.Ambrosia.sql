INSERT into `statkeystore`
SELECT  
`entityId` , 
"default" AS  `domain` , 
 "__global__" AS  `world` ,  
 `category` ,  
 `stat` ,  
 `value` 
FROM  
`stats` ,  
`statentity` 
WHERE  
`player` =  `name` AND  
`type` =  'player'