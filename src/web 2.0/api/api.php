<?php
/**
API file for beardstat web API.
*/
define("BEARDSTAT_API_DIR", dirname(__FILE__) . "\\");
require_once BEARDSTAT_API_DIR . 'config.php';
$bs_db = new mysqli(BS_DB_HOST,BS_DB_USER,BS_DB_PASS,BS_DB_DB);
if($bs_db->connect_errno > 0){
    die('Unable to connect to database [' . $bs_db->connect_error . ']');
}
include_once BEARDSTAT_API_DIR . 'sql.php';
include_once BEARDSTAT_API_DIR . 'player.class.php';
include_once BEARDSTAT_API_DIR . 'tabs.class.php';

/*$bs_db->real_query("SELECT DISTINCT(name) FROM " . BS_DB_PREFIX . "_entity;");
$res = $bs_db->store_result();
echo $res->num_rows . "<hr>";
while($row = $res->fetch_assoc()){
  echo $row['name'] . "<br>";
}*/
?>