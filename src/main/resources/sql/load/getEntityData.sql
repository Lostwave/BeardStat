SELECT 
`domainId`,
`worldId`,
`categoryId`,
`statisticId`,
`value` 
FROM 
`${PREFIX}_keystore` as k,
`${PREFIX}_entity` as e
WHERE 
e.`entityId`= k.`entityId` AND
e.`name` = ? AND
e.`type` = ?