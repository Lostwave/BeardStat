<?php
include 'api/api.php'; 
$score = new SScoreboad('config/scoreboard.json');
?>
<table border=1>
<tr><th>Rank</th><th>Player</th>
<?php 
while($score->have_field()){
 echo "<th>" . $score->the_field_name() . "</th>";
}
$score->reset_field();
?></tr>
</tr>
<?php 
while($score->have_entry()){
?><tr><td><?php echo $score->the_rank(); ?></td><td><?php echo $score->the_player_name(); ?></td><?php 
 while($score->have_field()){
  echo "<td class=\"" . $score->the_field_name() . "\">" . $score->the_field_value() . "</td>"; 
 } 
 echo "</tr>";
 $score->reset_field();
}
?>
</table>