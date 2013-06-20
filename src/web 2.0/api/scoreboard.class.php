<?php

Class SScoreboad{

 private $sql;

 

 function __construct($file){
   
  $domainLookup    = getLookup("domain", "domain");
  $worldLookup     = getLookup("world", "world");
  $categoryLookup  = getLookup("category", "category");
  $statisticLookup = getLookup("statistic", "statistic");
   

  $data = json_decode(file_get_contents($file));

  $sqlSelect = "$[PREFIX]_entity.`name` as `player`, $[PREFIX]_entity.`entityId`";
  $sqlFrom   = "$[PREFIX]_value as value, $[PREFIX]_entity, $[PREFIX]_domain";

  $sqlWhere  = "$[PREFIX]_entity.`type` = \"player\"";
  
  $sqlJoin  = "";
  
  $id = 0;
  foreach($data as $entry){
   //{"label":"diamonds mined","domain":".*","world":".*","cat":"blockdestroy","stat":"diamondore"}
   $sqlSelect .= ",\n vk$id.`value` as `" . $entry->label . "`";
   
  
   $did = $domainLookup[$entry->domain]["domainId"];
   $wid = $worldLookup[$entry->world]["worldId"];
   $cid = $categoryLookup[$entry->cat]["categoryId"];
   $sid = $statisticLookup[$entry->stat]["statisticId"];
   $s = <<<SQL
      LEFT JOIN (
      $[PREFIX]_value as vk$id
      )
       ON (
       vk$id.entityId             = $[PREFIX]_entity.`entityId` AND
       vk$id.`domainId`           = $did AND
       vk$id.`worldId`            = $wid AND
       vk$id.`categoryId`         = $cid AND
       vk$id.`statisticId`        = $sid
       ) USING ($[PREFIX]_entity)
SQL;
   $sqlJoin .= $s . "\n";
   $id++;
  }


  $this->sql = "SELECT $sqlSelect \n FROM $sqlFrom \n$sqlJoin \n group by `player` ORDER BY `" . $data[0]->label . "` DESC";
  $this->sql = str_replace("$[PREFIX]", BS_DB_PREFIX,$this->sql);
  echo $this->sql;
 }
}

?>