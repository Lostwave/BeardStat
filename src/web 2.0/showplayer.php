<?php
include 'api/api.php';
$p = new SPlayer($_GET['playerName']);
$res = $p->getStats();
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

$tabs = new StatTabs("tabs.json");

while($tabs->have_tabs()){
 echo $tabs->the_tab_name() . "</br>";
 while($tabs->have_headings()){
  echo ".." . $tabs->the_heading_name() . "</br>";
  while($tabs->have_entries()){
   echo "...." . $tabs->the_entry() . " = " . $tabs->the_entry_value_for_player($p) . "</br>";
  }
 }
}


?>