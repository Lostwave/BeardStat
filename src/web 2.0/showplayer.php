<?php
include 'api/api.php';
$p = new SPlayer($_GET['playerName']);
$tabs = new StatTabs("tabs.json");
define('BS_TITLE',"Player stats");
include 'templates/header.php';

?>
<div class="span-8 offset2">
<div class="tab-stats">
<ul class="nav nav-tabs">
<?php 

$tabsHtml = "";
$contentHtml ="";

while($tabs->have_tabs()){
 $id = $tabs->the_tab_id();
  $name = $tabs->the_tab_name();
  $tabsHtml .= "<li><a href=\"#$id\" data-toggle=\"tab\">$name</a></li>";
  $contentHtml .="<div id=\"$id\"class=\"tab-pane fade\">";
 while($tabs->have_headings()){
  
  $contentHtml .= "<h3>" . $tabs->the_heading_name() ."</h3>";
  while($tabs->have_entries()){
   $contentHtml .= $tabs->the_entry_label() . " = " . $tabs->the_entry_value_for_player($p)->getValueFormatted() . "</br>";
  }
  
 }
 $contentHtml .="</div>";
}
echo $tabsHtml;
echo "</ul><div class=\"tab-content\">";
echo $contentHtml;
echo "</div>";


?>
</div>
</div>