<?php
Class SPlayerStat{
 public $domain;
 public $world;
 public $category;
 public $statistic;
 public $value;
 
 function __construct( $domain, $world, $category, $statistic, $value){
  $this->domain = $domain;
  $this->world = $world;
  $this->category = $category;
  $this->statistic = $statistic;
  $this->value = $value;
 }
 
 function __toString(){
  StatTabs::$statLookup[$this->statistic]["format"];
  StatTabs::$statLookup[$this->statistic]["convert"];
 }
 
}
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

 /**
  * Returns an array of stats based on the given elements
  * @param string $domainQry
  * @param string $worldQry
  * @param string $categoryQry
  * @param string $statisticQry
  * @return multitype:SPlayerStat
  */
 function getStats($domainQry='.*',$worldQry='.*',$categoryQry='.*',$statisticQry='.*'){
  //catch nulls to allow any parameter to be passed
  $domainQry = is_null($domainQry) ? '.*' : $domainQry;
  $worldQry = is_null($worldQry) ? '.*' : $worldQry;
  $categoryQry = is_null($categoryQry) ? '.*' : $categoryQry;
  $statisticQry = is_null($statisticQry) ? '.*' : $statisticQry;
   
  $domainPattern = '/' . $domainQry . '/';
  $worldPattern = '/' . $worldQry . '/';
  $categoryPattern = '/' . $categoryQry . '/';
  $statisticPattern = '/' . $statisticQry . '/';
   
  $results = array();
  foreach($this->data as $domainId => $domain){
   if(preg_match($domainPattern,$domainId)){
    foreach($domain as $worldId => $world){
     if(preg_match($worldPattern,$worldId)){
      foreach($world as $categoryId => $category){
       if(preg_match($categoryPattern,$categoryId)){
        foreach($category as $statisticId => $value){
         if(preg_match($statisticPattern,$statisticId)){
          $results[] = new SPlayerStat($domainId,$worldId,$categoryId,$statisticId,$value);
         }
        }
       }
      }
     }
    }
   }
  }
  return $results;
 }

 /**
  * Returns a singular object, the result of the array of getStats() sum()'d
  * @param string $domainQry
  * @param string $worldQry
  * @param string $categoryQry
  * @param string $statisticQry
  * @return SPlayerStat
  */
 function getStat($domainQry='.*',$worldQry='.*',$categoryQry='.*',$statisticQry='.*'){
  $res = $this->getStats($domainQry,$worldQry,$categoryQry,$statisticQry);
  $v = new SPlayerStat($domainQry,$worldQry,$categoryQry,$statisticQry,0);
  foreach($res as $r){
   $v->value += $r->value;
  }
  
  return $v;
   
 }

}

?>