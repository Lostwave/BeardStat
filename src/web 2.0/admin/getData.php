<?php
require 'session.php';
require 'api/api.php';
//Provide data table for admin pages
if(!isset($_chk)){
$_chk = $_GET['id'];
}
$valid = $_chk == "domain" || $_chk == "world" || $_chk == "category" || $_chk == "statistic";
if(!$valid){die()}
echo json_encode(getLookup($_chk, $_chk));
unset($_chk);
?>

