<?php
require 'session.php';
require 'api/api.php';
//Provide data table for admin pages
if(!isset($_chk)){
$_chk = $_GET['id'];
}
$valid = $_chk == "scoreboards" || $_chk == "tabs";
if(!$valid){die();}
if($_SERVER['REQUEST_METHOD'] == "POST"){
  $json = file_get_contents('php://input');
  if(json_decode($json) !== NULL){
    if(copy("../config/$_chk.json","../config/$_chk.json.backup")){
      file_put_contents("../config/$_chk.json",$json);
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