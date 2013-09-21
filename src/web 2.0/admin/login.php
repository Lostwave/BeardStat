<?php
include '../api/api.php';
include 'session.php';


if(!IS_AUTHED){
 if(isset($_POST['pw']) && $_POST['pw'] === BS_PW){
  $_SESSION['authed'] = true;
  header("Location: index.php");
  die();
 }
 
 define('BS_TITLE','login');
 include '../templates/header.php';
 ?>
<form action="login.php" method="POST">
	password: <input type="password" name="pw"><br/>
	<input type="submit" value="login">
</form>
<?php
include '../templates/footer.php';
}
else
{
 header("Location: index.php");
}
?>