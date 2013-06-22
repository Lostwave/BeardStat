<?php 
$p = new SPlayer($_GET['playerName']);
$tabs = new StatTabs("tabs.json");
?>
<h2>
		<img
			src="https://minotar.net/avatar/<?php echo $p->name; ?>/100">
		<?php echo $p->name; ?>
	</h2>
	<div class="tab-stats span7">
		<ul class="nav nav-tabs">
			<?php 
			//Print headings
			$firstTab = true;
			while($tabs->have_tabs()){
 $id = $tabs->the_tab_id();
 $name = $tabs->the_tab_name();
 ?>
			<li class="<?php echo ($firstTab ? "active":"") ?>"><a
				href="#<?php echo $id;?>" data-toggle="tab"><?php echo $name;?></a></li>
			<?php
			$firstTab = false;
}
echo "</ul><div class=\"tab-content\">";
$tabs->reset_tabs();
$dump = "";
$firstTab = true;
while($tabs->have_tabs()){
  $id = $tabs->the_tab_id();
  $name = $tabs->the_tab_name();
  
  $dump .= "Making tab $id:$name\n";//DUMP
  
  
  echo "</ul><div id=\"$id\" class=\"tab-pane fade " . ($firstTab ? "active in":"") . "\">";
  if($firstTab){
$firstTab=false;
}

echo "<table class=\"table table-bordered\">";
while($tabs->have_headings()){
  
  echo"<tr><td colspan=\"2\"><h3>" . $tabs->the_heading_name() ."</h3></td></tr>";
  $dump .= "  Making heading " . $tabs->the_heading_name() . "\n";//DUMP
  while($tabs->have_entries()){
   $dump .= "    Making entry " . $tabs->the_entry_label() . "\n";//DUMP
   echo "<tr><td>" . $tabs->the_entry_label() . "</td><td>" . $tabs->the_entry_value_for_player($p)->getValueFormatted() . "</td></tr>";
  }
  $tabs->reset_entries();

 }
 $tabs->reset_headings();
 echo "</table></div>";
}
$tabs->reset_tabs();

echo "</div>";


?>
</div>