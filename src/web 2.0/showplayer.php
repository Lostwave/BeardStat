<?php
include 'api/api.php';
$p = new SPlayer($_GET['playerName']);
$tabs = new StatTabs("tabs.json");
define('BS_TITLE',"Player stats");
include 'templates/header.php';

?>
<div class="span-8 offset2">
<h2><img src="https://minotar.net/avatar/<?php echo $_GET['playerName']; ?>/100"><?php echo $_GET['playerName']; ?></h2>
<div class="tab-stats">
<ul class="nav nav-tabs">
<?php 

$tabsHtml = "";
$contentHtml ="";
$firstTab = true;
while($tabs->have_tabs()){
 $id = $tabs->the_tab_id();
  $name = $tabs->the_tab_name();
  $tabsHtml .= "<li class=\"" . ($firstTab ? "active":"") . "\"><a href=\"#$id\" data-toggle=\"tab\">$name</a></li>";
  $contentHtml .="<div id=\"$id\"class=\"tab-pane fade " . ($firstTab ? "active in":"") . "\">";
  if($firstTab){$firstTab=false;}
  
  $contentHtml .= "<table class=\"table table-bordered\">";
 while($tabs->have_headings()){
  
  $contentHtml .= "<tr><td colspan=\"2\"><h3>" . $tabs->the_heading_name() ."</h3></td></tr>";
  while($tabs->have_entries()){
   $contentHtml .= "<tr><td>" . $tabs->the_entry_label() . "</td><td>" . $tabs->the_entry_value_for_player($p)->getValueFormatted() . "</td></tr>";
  }
  
 }
 $contentHtml .="</table></div>";
}
echo $tabsHtml;
echo "</ul><div class=\"tab-content\">";
echo $contentHtml;
echo "</div>";


?>
</div>
</div>