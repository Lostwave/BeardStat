<?php
include 'session.php';
unset($_SESSION['authed']);
header("location","login.php");
?>