<?php

/**********************************************************************************************
 This is a reversed-color version of the default layout. It's best suited for websites with a
 black or dark background. It will probably be best to remove the body{} declaration in the
 CSS, as it's only there if you're not including the layout into a pre-existing website. You
 should be able to find it on line 202.

 All comments have been removed from this default layout, but the script remains the same.

 This layout is also presented without any license or copyright. You are free to alter,
 dissect or borrow any of the code used in this layout. The BeardStat Web API, however does
 still come with a license and you should make sure you fully read and understand it before
 using it.

***********************************************************************************************
 This layout fits best into a space of at least 600px wide. 610px for a comfortable margin.
**********************************************************************************************/

require_once("beardstat.php");
$img_dir = "img/";
$ach_url = "";

$result = "";
$resulthead = "Results";

$playerins = "";
$toplistins = -1;

$error = "";
$errorno = -1;

function adderror($err="",$errno=-1) {
  global $error,$errorno;
  $error = ($err != "") ? "<div class=\"bserror\">".$err."</div>": "";
  $errorno = $errno;
}










if (isset($_GET['player']) && $_GET['player'] != "") {
  $playerins = al($_GET['player'],"0123456789_");
  $maxresults = 100;
  $player = BS_find_player($playerins,$maxresults);

  if ($player == -1) {
    adderror("Player name must be between 2 and 16 characters long. Please try again.",0);
  }
  else if (!$player) {
    adderror("The player you searched for either doesn't exist or hasn't recorded any statistics yet.",0);
  }
  else if (is_array($player)) {
    $resulthead = "Multiple Players Found";
    $result .= "<div class=\"bsheader_title\">".count($player)." Players Found for '".$playerins."'</div>
<table class=\"bsdata\" cellpadding=2 cellspacing=0>
<tr><td class=\"bsdata_head\" width=\"400\" nowrap>Which player did you mean?</td></tr>
";
    $rot = "bsdata";
    foreach ($player as $value) {
      $result .= "<tr><td class=\"".$rot."\" align=\"center\"><a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?player=".$value."\">".$value."</a></td></tr>\n";
      $rot = ($rot == "bsdata") ? "bsdata2": "bsdata";
    }
    if (count($player) >= $maxresults) $result .= "<tr><td class=\"".$rot."\" align=\"center\"><span class=\"bsempi\">Only the first <span class=\"bsemp\">".$maxresults."</span> results are shown.<br>Please use a more specific search.</span></td></tr>\n";
    $result .= "</table>";
  }
  else {
    $playerins = $player;

    if (BS_load_player($player)) {
      $insert = ($ach_url != "") ? "<div style=\"margin:0 0 5px 0;\"><a class=\"bs\" href=\"".$ach_url."?player=$player\">View $player's Achievements »</a></div>\n": "";
      $result .= "<div class=\"bsheader_title\">Statistics for $player</div>
$insert<table class=\"bsstat_outer\" width=\"595\" cellpadding=0 cellspacing=5><tr><td class=\"bstab_outer\" valign=\"bottom\">
<table class=\"bstable\" cellpadding=0 cellspacing=0><tr>\n";

      BS_reset_all_stats();
      $count = 0;
      while ($cat = BS_next_category()) {
        $insert = ($count == 0) ? "bstab_selected": "bstab_generic";
        $result .= "<td><div class=\"".$insert."\" id=\"bstabt_$count\" onmouseover=\"managetabs(this,'$count','bstab_highlight');\" onmouseout=\"managetabs(this,'$count','bstab_generic');\" onclick=\"changetab('$count');\">".htmlspecialchars($cat)."</div></td>";
        $count++;
      }
      $result .= "</tr></table>
</td></tr>
<tr><td class=\"bsstat_inner\" align=\"center\" valign=\"top\">";

      BS_reset_category();
      $count = 0;
      while ($cat = BS_next_category()) {
        $insert = ($count == 0) ? "bs_in_block": "bs_in_hidden";
        $result .= "<div class=\"".$insert."\" id=\"bstabx_$count\" align=\"center\">
<table class=\"bsdata\" width=\"570\" cellpadding=2 cellspacing=0>\n";
        $rot = "bsdata";

        while ($stat = BS_next_stat($cat)) {
          if ($stat[0] == 1) $result .= "<tr><td class=\"bsdata_head\" colspan=2>".htmlspecialchars($stat[1])."</td></tr>\n";
          else {
            $statname = ($temp = BS_search_list($stat[4],$stat[6])) ? "<a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?top=".$temp[0]."\" title=\"View Top ".BS_CFG_DEFAULTTOP." ".htmlspecialchars($temp[1])."\">".htmlspecialchars($stat[1])."</a>": htmlspecialchars($stat[1]);
            $statvalue = ($stat[3] !== "" && $stat[3] === 0 && $stat[2] != BS_CFG_ONLINETEXT) ? "-": htmlspecialchars($stat[2]);
            if ($statvalue == htmlspecialchars(BS_CFG_ONLINETEXT)) $statvalue = "<span class=\"bsempi\">".$statvalue."</span>";

            $result .= "<tr><td class=\"$rot\" width=\"210\" valign=\"top\">$statname</td><td class=\"$rot\" width=\"360\" align=\"center\" valign=\"top\">$statvalue</td></tr>\n";
            $rot = ($rot == "bsdata") ? "bsdata2": "bsdata";
          }
        }

        $result .= "</table>
</div>\n";
        $count++;
      }

      $result .= "</td></tr></table>";
    }
    else adderror("Well this is embarrassing. Somehow $player has reached a quantum state of existing and not existing at the same time. Refresh? Or maybe document this amazing scientific discovery.");
  }
}










else if (isset($_GET['top']) && is_numeric($_GET['top'])) {
  $ID = floor(abs($_GET['top']));
  if (BS_load_top_list($ID)) {
    $info = BS_loaded_top_list();
    $tinfo = BS_current_list($ID);

    $result .= "<div class=\"bsheader_title\">Top ".BS_CFG_DEFAULTTOP." ".htmlspecialchars($tinfo[1])."</div>
<table class=\"bsdata\" width=\"590\" cellpadding=2 cellspacing=0>
<tr><td class=\"bsdata\">".htmlspecialchars($tinfo[2])."</td></tr>
</table>
<table class=\"bsdata\" width=\"590\" cellpadding=2 cellspacing=0>
<tr><td class=\"bsdata_head\" width=\"60\">#</td><td class=\"bsdata_head\" width=\"170\" nowrap>Player</td><td class=\"bsdata_head\" width=\"360\" nowrap>Value</td></tr>\n";
    $rot = "bsdata";

    if ($info[0] == 0) $result .= "<tr><td class=\"$rot\" colspan=3 align=\"center\"><span class=\"bsempi\">No statistics for that category exist yet.</span></td></tr>\n";
    else {
      while ($toplist = BS_next_top_list()) {
        $result .= "<tr><td class=\"$rot\" width=\"60\" align=\"center\" valign=\"top\">".suffix($toplist[0])."</td><td class=\"$rot\" width=\"170\" valign=\"top\"><a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?player=".$toplist[1]."\">".$toplist[1]."</a></td><td class=\"$rot\" width=\"360\" align=\"center\" valign=\"top\">".htmlspecialchars($toplist[2])."</td></tr>\n";
        $rot = ($rot == "bsdata") ? "bsdata2": "bsdata";
      }
    }

    $prevlist = "&nbsp;";
    $nextlist = "&nbsp;";
    $prev = BS_current_list($ID-1);
    $next = BS_current_list($ID+1);
    if ($prev[0] == ($ID-1)) $prevlist = "<a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?top=".$prev[0]."\" title=\"".htmlspecialchars($prev[2])."\">« Top ".BS_CFG_DEFAULTTOP." ".htmlspecialchars($prev[1])."</a>";
    if ($next[0] == ($ID+1)) $nextlist = "<a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?top=".$next[0]."\" title=\"".htmlspecialchars($next[2])."\">Top ".BS_CFG_DEFAULTTOP." ".htmlspecialchars($next[1])." »</a>";

    $result .= "</table>
<br>
<table class=\"bstable\" width=\"100%\" cellpadding=0 cellspacing=0>
<tr><td valign=\"top\">$prevlist</td><td valign=\"top\" align=\"right\">$nextlist</td></tr>
</table>";
    $toplistins = $ID;
  }
}

























if ($error != "") $result = $error;



?>
<style><!--

body			{ background:#000000; }

table.bstable, table.bssearchbox, table.bsdata,
table.bsstat_outer, .bstext, .bsbutton, .bsselect,
div.bstab_generic, div.bstab_selected, div.bstab_highlight
			{ color:#FFFFFF; font:12px Verdana, Arial, sans-serif; }

table.bssearchbox	{ background:#222222 url("<?php echo $img_dir; ?>bs_search_mainbg.png") repeat-x bottom left; width:600px; border:1px solid #444444; }
td.bssearch_head	{ color:#FFFFFF; background:#777777 url("<?php echo $img_dir; ?>bs_search_head.png") repeat-x top left; font-size:14px; font-weight:bold; text-align:center; border:1px solid #444444; border-width:0 0 1px 0; }

table.bsdata		{ color:#000000; border:1px solid #444444; border-width:1px 0 0 1px; }
td.bsdata_head		{ color:#333333; background:#888888 url("<?php echo $img_dir; ?>bs_search_head.png") repeat-x bottom left; font-weight:bold; text-align:center; border:1px solid #444444; border-width:0 1px 1px 0; }
td.bsdata, td.bsdata2	{ background:#F0F0F0 url("<?php echo $img_dir; ?>bs_gfade.png") repeat-x bottom left; border:1px solid #444444; border-width:0 1px 1px 0; }
td.bsdata2		{ background-color:#C0C0C0; }

div.bstab_generic, div.bstab_selected, div.bstab_highlight
			{ color:#000000; background:#FFFFFF url("<?php echo $img_dir; ?>bs_gfade.png") repeat-x bottom left; border:1px solid #444444; padding:5px; margin:0 5px 0 0; cursor:pointer; }
div.bstab_selected	{ color:#FFFFFF; background-color:#161616; font-weight:bold; }
div.bstab_highlight	{ color:#000000; background-color:#D4D4D4; }

table.bsstat_outer	{ border:1px solid #333333; background:#000000 url("<?php echo $img_dir; ?>bs_mfade.png") repeat-x top left; }
td.bsstat_inner		{ border:1px solid #333333; background:#222222 url("<?php echo $img_dir; ?>bs_search_mainbg.png") repeat-x bottom left; padding:5px; }
td.bstab_outer		{  }
div.bs_in_block		{ display:block; }
div.bs_in_hidden	{ display:none; }

.bsemp			{ font-weight:bold; }
.bsempi			{ font-style:italic; }
.bschoose		{ color:#E03322; }
div.bserror		{ border:2px solid #FF3333; color:#000000; background:#FFE9E9; text-align:center; padding:5px; width:320px; }
.bsheader_title		{ font-weight:bold; font-size:16px; color:#FFFFFF; border:1px solid #FF0000; border-width:0 0 1px 0; padding:0 0 1px 0; margin:0 0 5px 0; width:100%; }

.bsform			{ display:inline; }
.bstext			{ color:#000000; background:#FFFFFF url("<?php echo $img_dir; ?>bs_text.png") repeat-x top left; border:1px solid #999999; margin:0; }
.bsbutton		{ font-weight:bold; color:#333333; background:#BBBBBB url("<?php echo $img_dir; ?>bs_search_head.png") repeat-x bottom left; border:1px outset #AAAAAA; margin:0; }
.bsselect		{ color:#000000; border:1px solid #999999; margin:0; padding:0; }

a.bs			{ color:#222222; font-weight:bold; text-decoration:none; }
a.bs:hover		{ color:#444444; }

--></style>
<script><!--

bstop_data = new Array();
<?php

BS_reset_list();
while ($toplist = BS_next_list()) {
  echo "bstop_data[".$toplist[0]."] = \"".htmlspecialchars($toplist[2])."\";\n";
}

?>

bscurrent_tab = "0";

function bschangetop(a) {
  var res = "&nbsp;";
  if (a != "-1" && typeof(bstop_data[a]) != "undefined") {
    res = '<table class="bsdata" cellpadding=2 cellspacing=0><tr><td class="bsdata"><span class="bsemp">Description:</span> '+bstop_data[a]+'</td></tr></table>';
  }
  document.getElementById('bstop_description').innerHTML = res;
}

function managetabs(a,b,c) {
  if (b != bscurrent_tab) a.className = c;
}

function changetab(a) {
  if (a != bscurrent_tab) {
    document.getElementById('bstabt_'+bscurrent_tab).className = 'bstab_generic';
    document.getElementById('bstabx_'+bscurrent_tab).className = 'bs_in_hidden';
    bscurrent_tab = a;
    document.getElementById('bstabt_'+a).className = 'bstab_selected';
    document.getElementById('bstabx_'+a).className = 'bs_in_block';
  }
}


//--></script>

<table class="bstable" style="width:100%;"><tr><td align="center" valign="top">

<table class="bssearchbox" cellpadding=2 cellspacing=0>
<tr><td class="bssearch_head">Statistics Search Form</td></tr>
<tr><td>



<table class="bstable" cellpadding=0 cellspacing=0 width="100%"><tr>
<td valign="middle">

<form class="bsform" id="player_form" action="<?php echo $_SERVER['PHP_SELF']; ?>" method="get">
<table class="bstable"><tr><td valign="middle" align="right">
<span class="bsemp">Find Player:</span>&nbsp;
</td><td valign="middle">
<input class="bstext" type="text" size="20" maxlength="16" name="player" id="player" value="<?php echo $playerins; ?>" onfocus="this.select();">
<input class="bsbutton" type="submit" value=" Go ">
</td></tr>
</table>
</form>

</td></tr>
<tr><td valign="middle">

<form class="bsform" id="list_form" action="<?php echo $_SERVER['PHP_SELF']; ?>" method="get">
<table class="bstable"><tr><td valign="middle">
or <span class="bsemp">View Top <?php echo BS_CFG_DEFAULTTOP; ?>:</span>&nbsp;
</td><td valign="middle">
<select class="bsselect" name="top" onchange="bschangetop(this.value);">
<option class="bschoose" value="-1">--- Choose One ---
<?php

BS_reset_list();
while ($toplist = BS_next_list()) {
  $insert = ($toplist[0] == $toplistins) ? " selected": "";
  echo "<option value=\"".$toplist[0]."\"$insert>".htmlspecialchars($toplist[1])."\n";
}

?>
</select>
<input class="bsbutton" type="submit" value=" Go ">
</td></tr>
</table>
<table width="100%" cellpadding=2 cellspacing=0><tr><td align="center"><span id="bstop_description">&nbsp;</span></td></tr></table>
</form>

</td>
</tr>
</table>



</td></tr>
</table>



<br><br>



<?php

if ($result != "") {
?>
<table class="bssearchbox" cellpadding=2 cellspacing=0>
<tr><td class="bssearch_head"><?php echo $resulthead; ?></td></tr>
<tr><td>

<?php echo $result; ?>

</td></tr>
</table>

</td></tr>
</table>
<?php
}





















?>