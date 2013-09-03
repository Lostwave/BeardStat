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
	//Check connectivity
 	$db = new mysqli(
 	$_POST['host'],
 	$_POST['user'],
 	$_POST['pass'],
 	$_POST['db'],
 	$_POST['port']
 	);
 	
 	if($db->connect_errno > 0){
    die('Unable to connect to database [' . mysqli_connect_error() . ']');
  }
  else
  {
  	echo "Connection to database established.";
  }


  if (!$db->real_query("SHOW TABLES")) {throw new Exception("Database Error [{$db->errno}] {$db->error}");}
  $prefix = $_POST['prefix'] . "_";
  $tCount = 0;
  $res = $db->store_result();

  echo "<h3>Checking for BeardStat tables</h3>";
  echo "<pre>";
  while($r = $res->fetch_array()){
    if(!strncmp($r[0], $prefix, strlen($prefix))){
    	echo "Table " . $r[0] . " found<br/>";
    	$tCount ++;
    }
  }
  $res->free();
  if($tCount == 0){
 	  die("Could not find table with prefix " + $_POST['prefix']);
  }
  else
  {
  	echo "" . $tCount . "/6 tables found<br/>";
  }
   echo "</pre>";
 //Write out to file
 $configPage = file_get_contents("api/config.php.defaults");
 foreach($_POST as $k => $v){
   $configPage = str_replace('${' . $k . '}', $v, $configPage);
 }

 if(file_put_contents("api/config.php", $configPage)===false){
   echo "<p> Failed to write to config file! Please save the section below as api/config.php, or configure files permissions to allow write access for the web server to " . dirname(__FILE__) . "/config </p>";
   echo "<pre>";
   echo htmlspecialchars($configPage);
   echo "</pre>";
 } 
 else
 {
 	 echo "<p>Configuration completed.</p>";
 }
}
?>
</div>
<?php 
include 'templates/footer.php';
?>