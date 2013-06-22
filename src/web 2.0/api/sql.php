<?php
/**
 * grab all data from a table, used for local lookup
 * @param string $element
 * @param string $key
 * @return multitype:
 */
function getLookup($element,$key){
 global $bs_db;
 $e = $bs_db->real_escape_string($element);
 $bs_db->real_query("SELECT * FROM " . BS_DB_PREFIX . "_" . $e);
 $res = $bs_db->store_result();
 while($r = $res->fetch_assoc()){
  $a[$r[$key]]=$r;
 }
 $res->free();
 return $a;
}

?>