<?php
require 'session.php';
require '../api/api.php';
//Provide data table for admin pages
 if(!IS_AUTHED){die("NOAUTH");}
if(!isset($_chk)){
$_chk = $_GET['id'];
}
$valid = $_chk == "scoreboards" || $_chk == "tabs";
if(!$valid){die();}
if($_SERVER['REQUEST_METHOD'] == "POST"){
  $raw = $HTTP_RAW_POST_DATA;
  $json = json_decode($raw);
  if($json !== NULL){
    if(copy("../config/$_chk.json","../config/$_chk.json.backup")){
      file_put_contents("../config/$_chk.json",json_encode($json));
      die('SUCCESS');
    }
    else
    {
      die('CANTBACKUP');
    }
  }
  else
  {
    die('INVALID');
  }

}
unset($_chk);
?>