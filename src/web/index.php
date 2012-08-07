<?php

/**********************************************************************************************
 Welcome to the default layout for the BeardStat Web API.
 =============================================================================================
 If you are new to using the BeardStat Web API, it is highly recommended that you read the
 readme file zipped with this. It contains every little piece of information you will need to
 know to configure the behavior of the API as well as detailed information about creating your
 own custom layout scripts.

 This default layout is coded to provide a good start to displaying statistics about your
 players on your website. It has the majority of the stats players care about, all in the
 right places. It can be include()'d as-is into any existing layout, or customized to fit
 perfectly to match the theme or color scheme of your website.

 The default layout also serves as a good example of how to best use the BeardStat Web API.
 All major parts of the code are commented with explanations of what's going on. You can
 follow along and customize each section you may need to.

 The best part about it all is that this layout is provided to you without any copyrights
 or licensing agreements. You can dissect the code, reuse portions of it in your own scripts
 or do basically anything you want with it. This only applies to this layout script, however.
 The BeardStat Web API itself comes with a license which you should read and make sure you
 understand completely before using. Copies of this license are found within the readme file
 as well as the source code of the BeardStat Web API itself.

 A good place to start customizing is to change around some of the colors used in the CSS. The
 gradients used should adapt to whatever color scheme you need. They are transparent PNGs.



 No cheese was eaten whilst coding this layout. I promise.

 ... Okay, maybe ONE Babybel.


***********************************************************************************************
 This layout fits best into a space of at least 600 pixels wide.
 Probably should allow 610 to be safe, though.
**********************************************************************************************/



// Include the API file. It should be in the same directory as this script or in the include_path.
// The Config file will be included by the API itself, so we only need to include the API.
require_once("beardstat.php");

// The directory where the images included with the default layout are located. If blank, the current
// directory is assumed. This will just be used directly before any image URL, so make sure there's
// a forward slash at the end if they're not in the same directory.
$img_dir = "img/";

// The URL to the BeardAch layout page. Leave blank to skip this feature.
//   (Added for retroactive support for the eventual BeardAch Web API.
//    As of this current time, it doesn't exist. If it happens to exist in
//    the future, hot dang! I guess I completed it! :-D)
$ach_url = "";





// We use $result to store the final data that will go into the results section of the HTML.
// $resulthead is the header text.
$result = "";
$resulthead = "Results";

// Used to insert values into the search forms.
$playerins = "";
$toplistins = -1;





// $error and $errorno are used for a generic error, set by adderror();
$error = "";
$errorno = -1;

function adderror($err="",$errno=-1) {
  // Used when an error occurs, to set $error to the error string.

  global $error,$errorno;

  $error = ($err != "") ? "<div class=\"bserror\">".$err."</div>": "";
  $errorno = $errno;
}










// If a player is being searched for.
if (isset($_GET['player']) && $_GET['player'] != "") {
  $playerins = al($_GET['player'],"0123456789_");

  // Search for the player specified.
  $maxresults = 100;
  $player = BS_find_player($playerins,$maxresults);

  if ($player == -1) {
    // -1 is returned if the player name was too long or not long enough.
    adderror("Player name must be between 2 and 16 characters long. Please try again.",0);
  }
  else if (!$player) {
    // If no player is found, FALSE is returned.
    adderror("The player you searched for either doesn't exist or hasn't recorded any statistics yet.",0);
  }
  else if (is_array($player)) {
    // If multiple players are found, an array of the player names is returned.
    $resulthead = "Multiple Players Found";
    $result .= "<div class=\"bsheader_title\">".count($player)." Players Found for '".$playerins."'</div>
<table class=\"bsdata\" cellpadding=2 cellspacing=0>
<tr><td class=\"bsdata_head\" width=\"400\" nowrap>Which player did you mean?</td></tr>
";
    $rot = "bsdata";
    // Run through each value in the returned array of players.
    foreach ($player as $value) {
      $result .= "<tr><td class=\"".$rot."\" align=\"center\"><a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?player=".$value."\">".$value."</a></td></tr>\n";
      $rot = ($rot == "bsdata") ? "bsdata2": "bsdata";
    }
    // If the number of returned players matches the max we sent, add the "more specific search" notice.
    if (count($player) >= $maxresults) $result .= "<tr><td class=\"".$rot."\" align=\"center\"><span class=\"bsempi\">Only the first <span class=\"bsemp\">".$maxresults."</span> results are shown.<br>Please use a more specific search.</span></td></tr>\n";
    $result .= "</table>";
  }
  else {
    // A single player is found. Display the statistics for them.
    $playerins = $player;

    if (BS_load_player($player)) {
      // Start creating the statistics table for $player. All the stats are shown at once and JavaScript is
      // used to change between the tabs created for each Main category.

      // Check the BeardAch URL, and include a link to it if it's set.
      $insert = ($ach_url != "") ? "<div style=\"margin:0 0 5px 0;\"><a class=\"bs\" href=\"".$ach_url."?player=$player\">View $player's Achievements »</a></div>\n": "";
      $result .= "<div class=\"bsheader_title\">Statistics for $player</div>
$insert<table class=\"bsstat_outer\" width=\"595\" cellpadding=0 cellspacing=5><tr><td class=\"bstab_outer\" valign=\"bottom\">
<table class=\"bstable\" cellpadding=0 cellspacing=0><tr>\n";


      // Reset all the categories then run through them to create our tabs. We use $count to
      // keep track of each one for changing them later with JavaScript.
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

      // Run through the Main categories again, but this time add all the stats with them.
      BS_reset_category();
      $count = 0;
      while ($cat = BS_next_category()) {
        $insert = ($count == 0) ? "bs_in_block": "bs_in_hidden";
        $result .= "<div class=\"".$insert."\" id=\"bstabx_$count\" align=\"center\">
<table class=\"bsdata\" width=\"570\" cellpadding=2 cellspacing=0>\n";
        $rot = "bsdata";

        // Run through each stat for the $cat-egory found.
        while ($stat = BS_next_stat($cat)) {
          // Is a sub-category.
          if ($stat[0] == 1) $result .= "<tr><td class=\"bsdata_head\" colspan=2>".htmlspecialchars($stat[1])."</td></tr>\n";
          else {
            // Is a stat. We check if the stat has a Top List with at least one of its details.
            // If one is found, we make the stat name into a link to link to that Top List.
            // Why do we do this? Convenience, my friend!
            $statname = ($temp = BS_search_list($stat[4],$stat[6])) ? "<a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?top=".$temp[0]."\" title=\"View Top ".BS_CFG_DEFAULTTOP." ".htmlspecialchars($temp[1])."\">".htmlspecialchars($stat[1])."</a>": htmlspecialchars($stat[1]);
            // Now we check the value to see if it's "nothing" and if so, we replace it with a dash.
            // Why do we do this? Style, my friend!
            $statvalue = ($stat[3] !== "" && $stat[3] === 0 && $stat[2] != BS_CFG_ONLINETEXT) ? "-": htmlspecialchars($stat[2]);
            // One last check to put italics on "Online Currently" if we're using RES_LastSession.
            // Why do we do this? Emphasis, my friend!
            if ($statvalue == htmlspecialchars(BS_CFG_ONLINETEXT)) $statvalue = "<span class=\"bsempi\">".$statvalue."</span>";

            // Now that we have the proper inserts we're gonna use... add the row!
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










// If a Top List is being viewed.
else if (isset($_GET['top']) && is_numeric($_GET['top'])) {
  // We can check BS_load_top_list(), as it will return false if that Top List ID doesn't exist.
  $ID = floor(abs($_GET['top']));
  if (BS_load_top_list($ID)) {
    // The Top List loaded fine, so load its info and the current Top List's base info.
    $info = BS_loaded_top_list();
    $tinfo = BS_current_list($ID);

    // Start the results table.
    $result .= "<div class=\"bsheader_title\">Top ".BS_CFG_DEFAULTTOP." ".htmlspecialchars($tinfo[1])."</div>
<table class=\"bsdata\" width=\"590\" cellpadding=2 cellspacing=0>
<tr><td class=\"bsdata\">".htmlspecialchars($tinfo[2])."</td></tr>
</table>
<table class=\"bsdata\" width=\"590\" cellpadding=2 cellspacing=0>
<tr><td class=\"bsdata_head\" width=\"60\">#</td><td class=\"bsdata_head\" width=\"170\" nowrap>Player</td><td class=\"bsdata_head\" width=\"360\" nowrap>Value</td></tr>\n";
    $rot = "bsdata";

    // If no results were actually loaded, add a row saying so.
    if ($info[0] == 0) $result .= "<tr><td class=\"$rot\" colspan=3 align=\"center\"><span class=\"bsempi\">No statistics for that category exist yet.</span></td></tr>\n";
    else {
      // Run through the Top List results.
      while ($toplist = BS_next_top_list()) {
        $result .= "<tr><td class=\"$rot\" width=\"60\" align=\"center\" valign=\"top\">".suffix($toplist[0])."</td><td class=\"$rot\" width=\"170\" valign=\"top\"><a class=\"bs\" href=\"".$_SERVER['PHP_SELF']."?player=".$toplist[1]."\">".$toplist[1]."</a></td><td class=\"$rot\" width=\"360\" align=\"center\" valign=\"top\">".htmlspecialchars($toplist[2])."</td></tr>\n";
        $rot = ($rot == "bsdata") ? "bsdata2": "bsdata";
      }
    }

    // Check previous and next Top List categories and load up their data to create previous and next links.
    // Why do we do this? I think I already said convenience as a reason before, but yeah, convenience, my friend! :-D
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

























// If we've encountered an error anywhere, change the $result to that error.
if ($error != "") $result = $error;



?>
<style><!--

  /** All the major tables and elements have their default font set. **/
table.bstable, table.bssearchbox, table.bsdata,
table.bsstat_outer, .bstext, .bsbutton, .bsselect,
div.bstab_generic, div.bstab_selected, div.bstab_highlight
			{ color:#222222; font:12px Verdana, Arial, sans-serif; }


  /** The Results and Search Form tables use this, and their headers use bssearch_head. **/
table.bssearchbox	{ background:#F0F0F0 url("<?php echo $img_dir; ?>bs_search_mainbg.png") repeat-x bottom left; width:600px; border:1px solid #000000; }
td.bssearch_head	{ color:#FFFFFF; background:#222222 url("<?php echo $img_dir; ?>bs_search_head.png") repeat-x top left; font-size:14px; font-weight:bold; text-align:center; border:1px solid #000000; border-width:0 0 1px 0; }


  /** The basic data tables. Used in a players' Statistics as well as when multiple players are found in a search. **/
table.bsdata		{ border:1px solid #000000; border-width:1px 0 0 1px; }
  /** The "header" rows, as well as sub-sections. **/
td.bsdata_head		{ color:#FFFFFF; background:#222222 url("<?php echo $img_dir; ?>bs_search_head.png") repeat-x bottom left; font-weight:bold; text-align:center; border:1px solid #000000; border-width:0 1px 1px 0; }
  /** Rotating color rows. bsdata, then bsdata2, then back and forth. Makes the tables easier to read. **/
td.bsdata, td.bsdata2	{ background:#F0F0F0 url("<?php echo $img_dir; ?>bs_gfade.png") repeat-x bottom left; border:1px solid #000000; border-width:0 1px 1px 0; }
td.bsdata2		{ background-color:#D0D0D0; }


  /** The Main category tabs when viewing a player's statistics. _generic = normal tab,
      _selected = when the tab has been selected, _highlight = when you mouse over it. **/
div.bstab_generic, div.bstab_selected, div.bstab_highlight
			{ background:#F0F0F0 url("<?php echo $img_dir; ?>bs_gfade.png") repeat-x bottom left; border:1px solid #999999; padding:5px; margin:0 5px 0 0; cursor:pointer; }
div.bstab_selected	{ color:#FFFFFF; background-color:#404040; font-weight:bold; }
div.bstab_highlight	{ background-color:#D9D9D9; }


  /** The overall table, outside and in, that is displayed when viewing a player's statistics.
      bsstat_outer in conjunction with the table's cellspacing is used to create the "border" effect. **/
table.bsstat_outer	{ border:1px solid #777777; background:#9F9F9F url("<?php echo $img_dir; ?>bs_mfade.png") repeat-x top left; }
  /** Revert back to the same color and background as used in table.bssearchbox for the "inside." **/
td.bsstat_inner		{ border:1px solid #777777; background:#F0F0F0 url("<?php echo $img_dir; ?>bs_search_mainbg.png") repeat-x bottom left; padding:5px; }
  /** Not "used" but is the table cell the Tabs are in. **/
td.bstab_outer		{  }
  /** Set by the changetab() JavaScript function to make the currently selected tab's stats visible
      and all the rest invisible. **/
div.bs_in_block		{ display:block; }
div.bs_in_hidden	{ display:none; }


  /** Basic "emphasis" of bold. More "flexibility" than just <b>, they say... **/
.bsemp			{ font-weight:bold; }
  /** "Emphasis" of italic. **/
.bsempi			{ font-style:italic; }
  /** Used to change the color of the non-Top List first entry in the Top List <select> drop-down. **/
.bschoose		{ color:#E03322; }
  /** The error <div> used when an error occurs. **/
div.bserror		{ border:2px solid #FF3333; color:#000000; background:#FFE9E9; text-align:center; padding:5px; width:320px; }
  /** The header <div>s used at the top of results and such. **/
.bsheader_title		{ font-weight:bold; font-size:16px; color:#333333; border:1px solid #990000; border-width:0 0 1px 0; padding:0 0 1px 0; margin:0 0 5px 0; width:100%; }


  /** Makes the <form>s used be inline instead of block. **/
.bsform			{ display:inline; }
  /** Text boxes. **/
.bstext			{ background:#FFFFFF url("<?php echo $img_dir; ?>bs_text.png") repeat-x top left; border:1px solid #666666; margin:0; }
  /** Buttons. **/
.bsbutton		{ font-weight:bold; color:#FFFFFF; background:#555555 url("<?php echo $img_dir; ?>bs_search_head.png") repeat-x bottom left; border:1px outset #444444; margin:0; }
  /** The Top Lists' <select>. **/
.bsselect		{ border:1px solid #999999; margin:0; padding:0; }


  /** Links. **/
a.bs			{ color:#000000; font-weight:bold; text-decoration:none; }
a.bs:hover		{ color:#999999; }

--></style>
<script><!--

// Define the Top List descriptions for displaying when the <select> is changed.
bstop_data = new Array();
<?php

BS_reset_list();
while ($toplist = BS_next_list()) {
  echo "bstop_data[".$toplist[0]."] = \"".htmlspecialchars($toplist[2])."\";\n";
}

?>

// Set the current tab to be the first one, as that defaults to be selected.
bscurrent_tab = "0";

function bschangetop(a) {
  // Changes the Top List description to the one specified by "a" or nothing if "a" isn't one.
  var res = "&nbsp;";
  if (a != "-1" && typeof(bstop_data[a]) != "undefined") {
    res = '<table class="bsdata" cellpadding=2 cellspacing=0><tr><td class="bsdata"><span class="bsemp">Description:</span> '+bstop_data[a]+'</td></tr></table>';
  }
  document.getElementById('bstop_description').innerHTML = res;
}

function managetabs(a,b,c) {
  // Used when a tab is moused over, to change it to the appropriate class.
  if (b != bscurrent_tab) a.className = c;
}

function changetab(a) {
  // Changes the currently selected tab to "a", reverting the old tab
  // back to _generic and the new tab to _selected.
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

// Reset the Top Lists and go through them to display the <select> list.
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

// Only display the Results table if it isn't a blank string.
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




















// Whitespace is healthy for you.

?>