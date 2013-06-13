<?php

function getElementTable($element){
  global $bs_db;
  $e = $bs_db->real_escape_string($element);
  echo "SELECT * FROM " . BS_DB_PREFIX . "_" . $e;
  $bs_db->real_query("SELECT * FROM " . BS_DB_PREFIX . "_" . $e);
  $res = $bs_db->store_result();
  while($r = $res->fetch_assoc()){
    $a[]=$r;
  }
  $res->free();
  return $a;
}

function getLookup($element,$key){
 global $bs_db;
 $e = $bs_db->real_escape_string($element);
 echo "SELECT * FROM " . BS_DB_PREFIX . "_" . $e;
 $bs_db->real_query("SELECT * FROM " . BS_DB_PREFIX . "_" . $e);
 $res = $bs_db->store_result();
 while($r = $res->fetch_assoc()){
  $a[$r[$key]]=$r;
 }
 $res->free();
 return $a;
}

?>