<?php

Class StatTabs{
 
 private $data;
 
 private $tabIdx = -1;
 private $headingIdx = -1;
 private $entryIdx = -1;
 
 
 public static $statLookup = null;
 
 /**
  * Bootstrap lookup table
  */
 static function init(){
  StatTabs::$statLookup = getLookup("statistic","statistic");
 }
 
 /**
  * Load a tabs definition from a file
  * @param unknown $file
  */
 function __construct($file){
  $this->data = json_decode(file_get_contents(BEARDSTAT_API_DIR . "..\\config\\" . $file));
 }
 
 function reset_tabs(){
  $this->tabIdx = -1;
 }
 
 function reset_headings(){
  $this->headingIdx = -1;
 }
 
 function reset_entries(){
  $this->entryIdx = -1;
 }
 
 function reset_all(){
  $this->reset_tabs();
  $this->reset_headings();
  $this->reset_entries();
 }
 /**
  * Main loop, increments the tab index and checks if it is less than 
  */
 function have_tabs(){
   $this->tabIdx ++;
   if($this->tabIdx < count($this->data)){
    //start of loop, reset the counters below us
    $this->reset_headings();
    $this->reset_entries();
    
    return true;    
   }
   else
   {
    return false;
   }
 }
 
 function the_tab_name(){
   return $this->data[$this->tabIdx]->tabName;
 }
 
 function have_headings(){
  $this->headingIdx ++;
  if($this->headingIdx < count($this->data[$this->tabIdx])){
   $this->reset_entries();
   return true;
  }
  else
  {
   return false;
  }
 }
 
 function the_heading_name(){
  return $this->data[$this->tabIdx]->headings[$this->headingIdx]->headingName;
 }
 
 function have_entries(){
  $this->entryIdx ++;
  return ($this->entryIdx < count($this->data[$this->tabIdx]->headings[$this->headingIdx]->display));
 }
 
 function the_entry(){
  return $this->data[$this->tabIdx]->headings[$this->headingIdx]->display[$this->entryIdx];
 }
 
 function the_entry_value_for_player($player){
  $parts = explode("::",$this->the_entry());
  for($i = 0;$i<count($parts);$i++){
    $parts[$i] = $parts[$i] === "*" ? ".*" : $parts[$i];  
  }
  $domainId = $parts[0];
  $worldId  = $parts[1];
  $catId    = $parts[2];
  $statId   = $parts[3];
  return $player->getStat($domainId,$worldId,$catId,$statId);
 }
 
 function the_entry_label(){
  $r = explode("::",$this->the_entry());
  return isset(StatTabs::$statLookup[$r[3]]) ? StatTabs::$statLookup[$r[3]]["name"] : '[[' . $r[3] . ']]'; 
 }
 
 
 
 
}
StatTabs::init();
//var_dump(StatTabs::$statLookup);

?>