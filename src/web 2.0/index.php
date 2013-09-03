<?php 
include 'api/api.php';
define('BS_TITLE', "Main page");
include 'templates/header.php';

?>
<div class="hero-unit">
<h1>Search players</h1>
  <p>Find your friends and enemies<img src="http://media-mcw.cursecdn.com/a/a7/Heart.svg" style="width:16px;height:32px;image-scale:cover;"></p>
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
<p>Find out who's got too much time to play, and who's most likely to fall of a ledge.</p>
<?php 
$score = new SScoreboard('config/scoreboards.json');
include 'templates/scoreboard-select.php';
?>
</div>

<?php include 'templates/footer.php';?>