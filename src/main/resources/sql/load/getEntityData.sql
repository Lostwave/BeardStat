SELECT 
`domain`,
`world`,
`category`,
`statistic`,
`value` 
FROM 
`${PREFIX}_value` as k,
`${PREFIX}_entity` as e,
`${PREFIX}_domain` as d,
`${PREFIX}_world` as w,
`${PREFIX}_category` as c,
`${PREFIX}_statistic` as s

WHERE 

d.`domainId`    = k.`domainId`    AND 
w.`worldId`     = k.`worldId`     AND 
c.`categoryId`  = k.`categoryId`  AND 
s.`statisticId` = k.`statisticId` AND 

e.`entityId`= k.`entityId` AND
e.`entityId` = ?;