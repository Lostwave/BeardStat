<?php
Class SPage{

  public $id = "";
  public $data = array();
  public $player = null;

  function __construct($pageId) {

    global $bs_db;
    $id = $bs_db->real_escape_string($pageId);
    $sql = <<<EOF
    SELECT 
    `domain`,
    `world`,
    `category`,
    `statistic`,
    FROM 
    `$[PREFIX]_web_page` as `wp`,
    `$[PREFIX]_entity` as `e`,
    `$[PREFIX]_domain` as `d`,
    `$[PREFIX]_world` as `w`,
    `$[PREFIX]_category` as `c`,
    `$[PREFIX]_statistic` as `s`
    WHERE 
    `d`.`domainId`    = `wp`.`domainId`    AND 
    `w`.`worldId`     = `wp`.`worldId`     AND 
    `c`.`categoryId`  = `wp`.`categoryId`  AND 
    `s`.`statisticId` = `wp`.`statisticId` AND 
    `e`.`entityId`    = `wp`.`pageId` =
EOF;

    $sql .= $this->id . " ORDER BY `wp.`order`";
    $sql = str_replace("$[PREFIX]", BS_DB_PREFIX,$sql);
    
    $bs_db->real_query($sql);
    $res = $bs_db->store_result();
    if (!$res) {
      throw new Exception("Database Error [{$bs_db->errno}] {$bs_db->error}");
    }
    while($row = $res->fetch_assoc()){
      $this->data[] = $row;
    }
    $res->free();

  }

  function getData($player){
    
  }
}
?>