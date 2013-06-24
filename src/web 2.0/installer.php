<?php 
define('BS_TITLE',"Installer");
include 'templates/header.php';
?>
<div class="span8">
<h1>BeardStat web interface installer</h1>
<?php 
if(file_exists('api/config.php')){
die("<p>Existing config detected installed disabled, delete api/config.php to use installer</p>"); 
}

if(!isset($_POST['host'])){
?>
<form class="form-horizontal">
<div class="control-group">
  <label class="control-label">Host:</label>
  <div class="controls">
    <input type="text" name="host">
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
  <button type="submit" class="btn btn-primary">Configure Web interface</button>
  </div>
</div>
</form>
<?php 
die();
}

?>


</div>

<?php 
include 'templates/footer.php';
?>