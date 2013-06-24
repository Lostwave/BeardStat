<?php 
define('BS_TITLE',"Installer");
include 'templates/header.php';
?>
<div class="span8">
	<h1>BeardStat web interface installer</h1>
	<?php 
	if(file_exists('api/config.php')){
	 die("<p>Existing config detected installed disabled, delete api/config.php to use installer</p><p>Please delete this installer file asap to prevent security issues in the future.</p>");
	}

	if(!isset($_POST['host'])){
?>
	<form class="form-horizontal" action="installer.php" method="post">
		<div class="control-group">
			<label class="control-label">Host and port:</label>
			<div class="controls">
				<input type="text" name="host"
					placeholder="Host name of mysql database"> <input type="number"
					name="port" class="input-mini"
					placeholder="port name of mysql database" value="3306">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">User:</label>
			<div class="controls">
				<input type="text" name="user">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Password:</label>
			<div class="controls">
				<input type="password" name="pass">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Database name:</label>
			<div class="controls">
				<input type="text" name="db">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Table prefix:</label>
			<div class="controls">
				<input type="text" name="prefix">
			</div>
		</div>
		<div class="control-group">
			<div class="controls">
				<button type="submit" class="btn btn-primary">Configure Web
					interface</button>
			</div>
		</div>
	</form>
	<?php 
}
else
{
 $configPage = file_get_contents("api/config.php.defaults");
 
 foreach($_POST as $k => $v){
   $configPage = str_replace('${' . $k . '}', $v, $configPage);
 }
 echo "<pre>";
 echo htmlspecialchars($configPage);
 echo "</pre>";
 file_put_contents("api/config.php", $configPage);
}
?>


</div>

<?php 
include 'templates/footer.php';
?>