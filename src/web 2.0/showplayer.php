<?php
ob_start();//Done for search handling auto redirecting
include 'api/api.php';
define('BS_TITLE',"Player stats");
include 'templates/header.php';

?>
<div class="span8 offset2" style="background-color: #FAFAFA">
<?php
if(!isset($_GET['search']) && isset($_GET['playerName'])){
include 'templates/player-tabs.php';
}
else
{
 include 'templates/player-select.php';
}
?>
</div>
<?php include 'templates/footer.php';?>