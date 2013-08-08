<form action="scoreboards.php" method="get">
<div class="input-append">
<select name="board">
<?php 
while($score->have_scoreboard()){
?><option value="<?php echo $score->the_scoreboard_id();?>"><?php echo $score->the_scoreboard_title();?></option><?php 
}
?>
</select>
<button class="btn" type="submit">View</button>
</div>
</form>
