<?php if(!isset($_GET['playerName'])){?>
<div class="span6 offset2">
	<h1>Search players</h1>
	<form action="showplayer.php" method="get">
		<div class="input-append">
			<input type="hidden" name="search"> <input type="text"
				name="playerName" placeholder="player name">
			<button class="btn" type="submit">Search!</button>
		</div>
	</form>
</div>
<?php }
else {
 //Process form, find matching players;
 global $bs_db;
 $sql = "SELECT `name` FROM " . BS_DB_PREFIX . "_entity WHERE `name` LIKE '%" . $bs_db->real_escape_string($_GET['playerName']) . "%'";
 $bs_db->real_query($sql);
 $res = $bs_db->store_result();

 $names = array();
 while($row = $res->fetch_object()){
  $names[] = $row->name;
 }
 $res->free();

 ?>
<h1>
	Search results:
	<?php echo strip_tags($_GET['playerName']); ?>
</h1>
<?php

if(sizeof($names) == 0){
echo "<span class='label label-important'>No users found matching that name</span>";
}
if(sizeof($names) == 1){
?>
<script>
window.location="showplayer.php?playerName=<?php echo $names[0];?>";
</script>
<?php die();
}
else{
?>

<table class="table" style="">
	<?php 
	foreach($names as $name){
?>
	<tr>
		
		<td><canvas class="head head-small" data-name="<?php echo $name;?>"></canvas>
		</td>
		<td><a href="showplayer.php?playerName=<?php echo $name;?>"><?php echo $name;?></a>
		</td>

	</tr>
	<?php } ?>
</table>
<?php
}
}


?>