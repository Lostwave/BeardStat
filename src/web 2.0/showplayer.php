<?php
include 'api/api.php';
$p = new SPlayer($_GET['playerName']);
$res = $p->getStats(null,null,null,".*clay.*");
echo "<ul>";
foreach($res as $domain => $worlds){
 echo "<li> $domain";
 echo "<ul>";

 foreach($worlds as $world => $cats){
  echo "<li> $world";
  echo "<ul>";

  foreach($cats as $cat => $stats){
   echo "<li> $cat";
   echo "<ul>";

   foreach($stats as $stat => $value){
    echo "<li> $stat = $value";
   }

   echo "</ul></li>";
  }

  echo "</ul></li>";
 }

 echo "</ul></li>";
}
echo "</ul>";
?>