<?php

/**
 * Class for accessing and iterating scoreboard data
 * @author James
 *
 */
Class SScoreboad{

 
 /**
  * Cached data, format undocumented, use at own risk
  * @var unknown
  */
 public $data;
 /**
  * Cached fields of dataset, in order from db
  * @var unknown
  */
 public $fields;
 
 /**
  * Title of scoreboard selected
  * @var unknown
  */
 public $title;
 
 
 //Iterator indexes
 private $dataIdx = -1;
 private $fieldIdx = -1;
 private $scoreboardsIdx = -1;
 //Cached scoreboard json
 private $scoreboards;

 /**
  * Load a json file 
  * @param string $file
  */
 function __construct($file){
  $this->scoreboards = json_decode(file_get_contents($file));
 }
 
 /**
  * Iterator for scoreboard metadata (id, title)
  * @return boolean if there is a scoreboard currently loaded
  */
 function have_scoreboard(){
  $this->scoreboardsIdx ++;
  return ($this->scoreboardsIdx < sizeof($this->scoreboards));
 }
 /**
  * Resets scoreboard iterator
  */
 function reset_scoreboard(){
  $this->scoreboardsIdx = 0;
 }
 /**
  * @return id of current scoreboard (in scoreboard loop)
  */
 function the_scoreboard_id(){
   return $this->scoreboards[$this->scoreboardsIdx]->id;   
 }
 
 /**
  * @return title of scoreboard (in scoreboard loop)
  */
 function the_scoreboard_title(){
  return $this->scoreboards[$this->scoreboardsIdx]->title;
 }
 
 /**
  * query db for scoreboard information
  * @param string $page id of scoreboard to load
  */
 function load($page){
  
  $domainLookup    = getLookup("domain", "domain");
  $worldLookup     = getLookup("world", "world");
  $categoryLookup  = getLookup("category", "category");
  $statisticLookup = getLookup("statistic", "statistic");
  
  foreach($this->scoreboards as $scoreboard){
   if($scoreboard->id == $page){
    $selectedScoreboard = $scoreboard;
    $this->title = $selectedScoreboard->title;
    break;
   }
  }
  if(!isset($selectedScoreboard)){
   die('no scoreboard selected');
  }
   
  $type = "player";//TODO - Make selectable in future

  $sqlSelect = "$[PREFIX]_entity.`name` as `player`";
  $sqlFrom   = " $[PREFIX]_entity";

  $sqlWhere  = "$[PREFIX]_entity.`type` = \"player\"";
  
  $sqlJoin  = "LEFT JOIN (stats_entity as vkp) ON (vkp.`type`=\"$type\" and vkp.entityId = $[PREFIX]_entity.entityId && $[PREFIX]_entity.scoreboardhide = 0)\n";
  
  $sqlOrder = array(); 
  $id = 0;
  foreach($selectedScoreboard->data as $entry){
  
   //{"label":"diamonds mined","domain":".*","world":".*","cat":"blockdestroy","stat":"diamondore"}

   //Generate expressions for join selection
   $didQuery = $this->_generate_sql_expression($domainLookup,$entry->domain,"domainId");
   $widQuery = $this->_generate_sql_expression($worldLookup,$entry->world,"worldId");
   $cidQuery = $this->_generate_sql_expression($categoryLookup,$entry->cat,"categoryId");
   $sidQuery = $this->_generate_sql_expression($statisticLookup,$entry->stat,"statisticId");

   $singular = startsWith($didQuery,"=") && startsWith($widQuery,"=") && startsWith($cidQuery,"=") && startsWith($sidQuery,"=");
   $sqlSelect .= ",\n SUM(" . ($singular ? "DISTINCT " : "") . "vk$id.`value`) as `" . $entry->label . "`";

   
   $cache["data"] = $statisticLookup[(isset($entry->alias)? $entry->alias : $entry->stat)];//Quick cache of the lookup table;
   $cache["lbl"] = $entry->label;
   $this->fields[] = $cache;
   
   $s = <<<SQL
      LEFT JOIN (
      $[PREFIX]_value as vk$id
      )
       ON (
       vk$id.entityId             = vkp.`entityId` AND
       vk$id.`domainId`           $didQuery AND
       vk$id.`worldId`            $widQuery AND
       vk$id.`categoryId`         $cidQuery AND
       vk$id.`statisticId`        $sidQuery
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
 
 /**
  * Reset entry loop
  */
 function reset_entry(){
  $this->dataIdx = -1;
 }
 
 /**
  * entry loop iterator check
  * @return boolean
  */
 function have_entry(){
  $this->dataIdx ++;
  return ($this->dataIdx < sizeof($this->data));
 }
 /**
  * Returns the rank of the current entry
  * @return number
  */
 function the_rank(){
  return $this->dataIdx +1;
 }
 
 /**
  * Returns the name of the player
  */
 function the_player_name(){
  return $this->data[$this->dataIdx]["player"];
 }
 /**
  * Resets field iterator
  */
 function reset_field(){
  $this->fieldIdx = -1;
 }
 
 /**
  * Field iterator
  * @return boolean
  */
 function have_field(){
  $this->fieldIdx ++;
  return ($this->fieldIdx < sizeof($this->fields));
 }
 
 /**
  * Name of current field
  */
 function the_field_name(){
  return $this->fields[$this->fieldIdx]["lbl"];
 }
 /**
  * Value of field for current entry, formatted
  * @return string
  */
 function the_field_value(){
  return formatStat($this->fields[$this->fieldIdx]["data"]["statistic"],$this->data[$this->dataIdx][$this->the_field_name()]);
 }
 /**
  * Raw value of field
  * @return number
  */
 function the_field_value_raw(){
  return $this->data[$this->dataIdx][$this->the_field_name()];
 }
 /**
  * The title of the scoreboard
  * @return unknown
  */
 function the_title(){
  return $this->title;
 }
 

 function _generate_sql_expression($lookupTable,$searchKey,$key){
  if(startsWith($searchKey,"$")){
    $table = idLookupTable($lookupTable,substr($searchKey,1),$key);
  }
  else
  {
  $table = $lookupTable[$searchKey][$key];
  }
   if(count($table) == 0){throw new exception("No table entry found for searchKey " . $searchKey);}
   if(count($table) == 1 ){
     return "= " . $table[0];
   }
   else
   {
     return "IN (" . implode(",",$table) . ")";
   }
 }
}



?>