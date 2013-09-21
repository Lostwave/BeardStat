<?php
include 'session.php';
noAuthGoLoginPage();

include '../api/api.php';

include '../templates/header.php';
if(IS_AUTHED){echo 'logged in';}
include '../templates/footer.php';
?>