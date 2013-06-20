<?php

function array_value($array, $key) {
 return $array[$key];
}

function arr_regex($array,$regex){
 foreach($array as $k=>$v){
  if(preg_match('/' . $regex . '/', $k)){
   return $v;
  }
 }
}

Class SScoreboad{


 private $data; 
 
 public $fields;
 
 private $dataIdx = -1;
 private $fieldIdx = -1;

 function __construct($file){
   
  $domainLookup    = getLookup("domain", "domain");
  $worldLookup     = getLookup("world", "world");
  $categoryLookup  = getLookup("category", "category");
  $statisticLookup = getLookup("statistic", "statistic");
   

  $data = json_decode(file_get_contents($file));
  $type = "player";//TODO - Make selectable in future

  $sqlSelect = "$[PREFIX]_entity.`name` as `player`";
  $sqlFrom   = " $[PREFIX]_entity";

  $sqlWhere  = "$[PREFIX]_entity.`type` = \"player\"";
  
  $sqlJoin  = "LEFT JOIN (stats_entity as vkp) ON (vkp.`type`=\"$type\" and vkp.entityId = $[PREFIX]_entity.entityId && $[PREFIX]_entity.scoreboardhide = 0)\n";
  
  $sqlOrder = array(); 
  $id = 0;
  foreach($data as $entry){
   //{"label":"diamonds mined","domain":".*","world":".*","cat":"blockdestroy","stat":"diamondore"}
   $sqlSelect .= ",\n vk$id.`value` as `" . $entry->label . "`";
   
  
   $did = array_value(arr_regex($domainLookup,$entry->domain),"domainId");
   $wid = array_value(arr_regex($worldLookup,$entry->world),"worldId");
   $cid = array_value(arr_regex($categoryLookup,$entry->cat),"categoryId");
   $sid = array_value(arr_regex($statisticLookup,$entry->stat),"statisticId");
   
   $cache["data"] = $statisticLookup[(isset($entry->alias)? $entry->alias : $entry->stat)];//Quick cache of the lookup table;
   $cache["lbl"] = $entry->label;
   $this->fields[] = $cache;
   
   $s = <<<SQL
      LEFT JOIN (
      $[PREFIX]_value as vk$id
      )
       ON (
       vk$id.entityId             = vkp.`entityId` AND
       vk$id.`domainId`           = $did AND
       vk$id.`worldId`            = $wid AND
       vk$id.`categoryId`         = $cid AND
       vk$id.`statisticId`        = $sid
       )
SQL;
   $sqlJoin .= $s . "\n";
   if(isset($entry->order)){
     $sqlOrder[$entry->order->idx]="`" . $entry->label ."` " . $entry->order->type;
   }
   $id++;
  }


  $sql = "SELECT $sqlSelect \n FROM $sqlFrom \n$sqlJoin \n group by `player`";
  if(sizeof($sqlOrder) > 0){
   $sql .= "ORDER BY " .implode(", ",$sqlOrder);
  }
  $sql = str_replace("$[PREFIX]", BS_DB_PREFIX,$sql);
  
  global $bs_db;
  $bs_db->real_query($sql ." LIMIT 10");
  
  $res = $bs_db->store_result();

  while($row = $res->fetch_assoc()){
   $this->data[] = $row;
  }
  $res->free();
  

 }
 
 function formatStat($stat,$value){
  if(!isset(StatTabs::$statLookup[$stat])){return $this->value;}
 
  switch(StatTabs::$statLookup[$stat]["formatting"]){
   case 'time':
    return gettimeformat($value);
    break;
   case 'timestamp':
    return date(BS_FORMAT_DATE,$value);
  }
 
  return $value;
 }
 
 function reset_entry(){
  $this->dataIdx = -1;
 }
 
 function have_entry(){
  $this->dataIdx ++;
  return ($this->dataIdx < sizeof($this->data));
 }
 
 function the_rank(){
  return $this->dataIdx +1;
 }
 
 function the_player_name(){
  return $this->data[$this->dataIdx]["player"];
 }
 
 function reset_field(){
  $this->fieldIdx = -1;
 }
 
 function have_field(){
  $this->fieldIdx ++;
  return ($this->fieldIdx < sizeof($this->fields));
 }
 
 function the_field_name(){
  return $this->fields[$this->fieldIdx]["lbl"];
 }
 function the_field_value(){
  return $this->formatStat($this->fields[$this->fieldIdx]["data"]["statistic"],$this->data[$this->dataIdx][$this->the_field_name()]);
 }
 
 
}

?>