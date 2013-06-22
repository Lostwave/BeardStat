<?php
/**
API file for beardstat web API.
*/
define("BEARDSTAT_API_DIR", dirname(__FILE__) . "\\");
(@require_once BEARDSTAT_API_DIR . 'config.php') or die("<h1>No API config found!</h1>");
$bs_db = new mysqli(BS_DB_HOST,BS_DB_USER,BS_DB_PASS,BS_DB_DB);
if($bs_db->connect_errno > 0){
    die('Unable to connect to database [' . $bs_db->connect_error . ']');
}
include_once BEARDSTAT_API_DIR . 'formating.php';
include_once BEARDSTAT_API_DIR . 'sql.php';
include_once BEARDSTAT_API_DIR . 'player.class.php';
include_once BEARDSTAT_API_DIR . 'scoreboard.class.php';
include_once BEARDSTAT_API_DIR . 'tabs.class.php';

/*$bs_db->real_query("SELECT DISTINCT(name) FROM " . BS_DB_PREFIX . "_entity;");
$res = $bs_db->store_result();
echo $res->num_rows . "<hr>";
while($row = $res->fetch_assoc()){
  echo $row['name'] . "<br>";
}*/

/**
 * Return value of array (used to bypass func()[x] issue
 * @param unknown $array
 * @param unknown $key
 * @return unknown
 */
function array_value($array, $key) {
 return $array[$key];
}
/**
 * Search associative array for value based on regex match of key
 * @param unknown $array
 * @param unknown $regex
 * @return unknown
 */
function arr_regex($array,$regex){
 foreach($array as $k=>$v){
  if(preg_match('/' . $regex . '/', $k)){
   return $v;
  }
 }
}
/**
 * Format stat
 * @param string $stat
 * @param number $value
 * @return string
 */
function formatStat($stat,$value){
 if(!isset(StatTabs::$statLookup[$stat])){return number_format($value);}

 switch(StatTabs::$statLookup[$stat]["formatting"]){
  case 'time':
   return gettimeformat($value);
   break;
  case 'timestamp':
   return date(BS_FORMAT_DATE,$value);
   break;

 }
 return number_format($value);
}
?>