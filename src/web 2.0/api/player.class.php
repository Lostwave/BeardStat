<?php

Class SPlayer{
  
  public $name = "";
  public $data = array();

  function __construct($playerName) {
    
    global $bs_db;
    $this->name = $bs_db->real_escape_string($playerName);
    $sql = <<<SQL
    SELECT 
`domain`,
`world`,
`category`,
`statistic`,
`value` 
FROM 
`$[PREFIX]_value` as `k`,
`$[PREFIX]_entity` as `e`,
`$[PREFIX]_domain` as `d`,
`$[PREFIX]_world` as `w`,
`$[PREFIX]_category` as `c`,
`$[PREFIX]_statistic` as `s`
WHERE 
`d`.`domainId`    = `k`.`domainId`    AND 
`w`.`worldId`     = `k`.`worldId`     AND 
`c`.`categoryId`  = `k`.`categoryId`  AND 
`s`.`statisticId` = `k`.`statisticId` AND 
`e`.`entityId`    = `k`.`entityId` AND
`e`.`name` =
SQL;
   $sql .= "'" . $this->name . "'";
   $sql = str_replace("$[PREFIX]", BS_DB_PREFIX,$sql);
   //echo $sql;//DEBUG
    $bs_db->real_query($sql);
    $res = $bs_db->store_result();
    if (!$res) {
    throw new Exception("Database Error [{$bs_db->errno}] {$bs_db->error}");
    }
    while($row = $res->fetch_assoc()){
      $domain = $row['domain'];
      $world = $row['world'];
      $cat = $row['category'];
      $stat = $row['statistic'];
      $value = $row['value'];
      $this->data[$domain][$world][$cat][$stat] = $value;
    }
    $res->free();
    
  }  
}

?>