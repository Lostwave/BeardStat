<?php 
include 'api/api.php';
define('BS_TITLE', "Main page");
include 'templates/header.php';

?>
<div class="hero-unit">
<h1>Search players</h1>
	<form action="showplayer.php" method="get">
		<div class="input-append">
			<input type="hidden" name="search"> <input type="text"
				name="playerName" placeholder="player name">
			<button class="btn" type="submit">Search!</button>
		</div>
	</form>
</div>

<div class="hero-unit">
<h1>Scoreboards</h1>
<?php 
$score = new SScoreboad('config/scoreboards.json');
include 'templates/scoreboard-select.php';
?>
</div>

<?php include 'templates/footer.php';?>