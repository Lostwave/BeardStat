<?php

/**********************************************************************************************
 BEARDSTAT WEB API LICENSE AND COPYRIGHT. IF YOU DO NOT AGREE TO THIS LICENSE,
 DO NOT USE THE BEARDSTAT WEB API.
 =============================================================================================

 The BeardStat Web API is presented as-is and provides no guarantee to its functionality or
 usability. Any damage to data, software or hardware you suspect may be caused by the
 BeardStat Web API is not intended and the author(s) are not liable for such damages. You use
 this software at your own discretion.

 The BeardStat Web API is coded in PHP by Richard "Furdabip" Moyer and is intended to be used
 with the MySQL settings for the BeardStat bukkit plugin for Minecraft. BeardStat is coded by
 Tehbeard and the BeardStat Web API is coded as a separate entity. Because of this, the
 author(s) cannot assist or comment on issues with the BeardStat plugin itself.


 WHAT YOU MAY DO WITH THE BEARDSTAT WEB API:
 ------------------------------------------
   You may edit the source code for the BeardStat Web API under the condition that all
   copyright notifications stay in place. You may add additional copyrights on the code you
   add, but never on code you merely edited. Notices about your edits may be provided,
   however.

   You may redistribute the BeardStat Web API under the condition that this readme be attached
   to it in any form. Usually this means zipping it with the API.

   You may recode and edit any of the non-API, non-readme files included with the BeardStat
   Web API. This includes and is limited to:

   - Recoding the default layout (default filename of index.php).
   - Reordering or editing the text of the format.ini and listformat.ini files.
   - Changing the config.php file to suit your configuration needs.

   You may distribute works created by you that are compatible with the BeardStat Web API in
   any way you see fit, including financially (if for some reason people want to pay you for
   them...). These works include but are not limited to Layout scripts, Statistic formats,
   Top List formats, or Configuration settings.

   All permissions listed here must not conflict with the "MAY NOT" section below.


 WHAT YOU MAY NOT DO WITH THE BEARDSTAT WEB API:
 ----------------------------------------------
   You may not claim the whole or any portion of the default BeardStat Web API as your own
   code or design.

   You may not steal or "borrow" portions of the code used in the BeardStat Web API to be used
   elsewhere.

   You may not eat Cheese whilst working on Layout scripts for the BeardStat Web API. This is
   strictly enforced. loljk.

   You may not provide Layout/other scripts to other people that use the BeardStat Web API in
   a way that is illegal or hides tracking or phishing code inside them. Why would you want to
   do this anyway?

   You may not remove any Copyright notices in this readme file or the BeardStat Web API. You
   must keep all original Copyrights if you plan on distributing the BeardStat Web API.


 A copy of this License and Copyright is provided inside the readme file for the BeardStat Web
 API. If this License and Copyright does not match, word for word, the same License and
 Copyright contained in this source code, it has been tampered with and you should not use
 this distribution of the BeardStat Web API.

 BeardStat Web API v0.1 r1
 by Richard Moyer.

**********************************************************************************************/





// Load the configuration file.
require_once("config.php");

// Check to make sure all the configuration options are available.
if (!isset($database_host,$database_user,$database_pass,$database_name,$time_format,$date_format,$time_online,$thousands_separator,$alphabetize_list,$default_top_list,$exclude_file,$include_all)) {
  echo "Missing some configuration settings!<br>\nPlease check to make sure that all configuration settings are present and set in the config file.";
  exit();
}

// Define the version constants.
define("BS_DEVVERSION","beta");
define("BS_VERSION",0.1);
define("BS_REVISION",1);
define("BS_AUTHOR","Richard Moyer");
define("BS_REVISIONAUTHOR","Richard Moyer");

// Define the config globals and data arrays.
define("BS_CFG_DBNAME",$database_name);
define("BS_CFG_TABLENAME","stats");

define("BS_CFG_TFORMAT",$time_format);
define("BS_CFG_DFORMAT",$date_format);
define("BS_CFG_ONLINETEXT",$time_online);
define("BS_CFG_TSEPARATOR",$thousands_separator);
define("BS_CFG_ALPHABETIZE",$alphabetize_list);
define("BS_CFG_DEFAULTTOP",$default_top_list);
define("BS_CFG_EXCLUDEFILE",$exclude_file);
define("BS_CFG_INCLUDEALL",$include_all);


$BSFormat = array();
$BSListFormat = array();

$BSLoaded = array();
$BSLoaded['current_player'] = "";
$BSLoaded['current_player_stats'] = array();
$BSLoaded['current_top_results'] = -1;
$BSLoaded['current_top_eresults'] = -1;
$BSLoaded['current_top_id'] = "";
$BSLoaded['current_top_category'] = "";
$BSLoaded['current_top_stat'] = "";
$BSLoaded['current_top_format'] = "%";
$BSLoaded['current_top_aord'] = "desc";
$BSLoaded['current_top_list'] = array();

$BSExcluded = array();
$BSExcluded_SQL = "";





  /*******************************************************************************************/
 /**] Introduction & Database Connection -------------------------------------------------[**/
/*******************************************************************************************/

if ($database_name == "") {
  echo "<table style=\"width:100%;\"><tr><td align=\"center\" valign=\"top\">

<div style=\"color:#333333;font:12px Verdana, Arial, sans-serif;width:440px;border:1px solid #DDDDDD;background:#F5F5F5;text-align:justify;padding:3px;\">
<div style=\"color:#000000;font-size:18px;font-weight:bold;text-align:center;\">Welcome to the BeardStat Web API!</div><br>
In order to get this working, you will need to open the configuration file to
setup your database information. You can find information about the configuration
settings in the <a href=\"readme.txt\" target=\"_blank\">readme.txt</a> file.<br><br>
It is highly recommend you fully read the readme included if you wish to code your
own layout instead of using the default one. If you just want to setup the stats
and worry about a custom layout later, you should at least read all of the
General Information sections in the readme.
</div>

</td></tr></table>";
  exit();
}

$con = @mysql_connect($database_host,$database_user,$database_pass);
if (!$con || !mysql_select_db($database_name)) {
  echo "Couldn't connect to the database.<br>\nError returned: <b>".mysql_error()."</b>";
  exit();
}
unset($database_host,$database_user,$database_pass,$database_name,$time_format,$date_format,
      $time_online,$thousands_separator,$alphabetize_list,$default_top_list,$exclude_file,
      $include_all);



  /*******************************************************************************************/
 /**] Load and Format External Files -----------------------------------------------------[**/
/*******************************************************************************************/

// Load the format.ini file, parse it and put its data into the $BSFormat array.
// We have to manually parse it, as it is not in any proper .ini format.

$lformat = @file("format.ini");
if (!$lformat) die("Cannot load format.ini.<br>\nMake sure it's in the same directory as this script or in the include_path.");
$curcat = "";
foreach ($lformat as $key => $value) {
  $temp = strpos($value,";");
  if ($temp !== false) $value = substr($value,0,$temp);
  $value = trim($value);
  $temp = explode("=",$value);
  if (count($temp) == 1) {
    // Isn't a stat entry.
    $temp = $temp[0];
    if (substr($temp,0,1) == "[" && substr($temp,-1) == "]") {
      // Is a new category.
      $curcat = substr($temp,1,-1);
      if (!isset($BSFormat[$curcat])) $BSFormat[$curcat] = array();
    }
    else if ($temp != "") {
      // Is a new sub category.
      if ($curcat != "") $BSFormat[$curcat][] = array(trim($temp));
    }
  }
  else {
    // Is a stat entry.
    $temp[0] = al($temp[0],"0123456789_-+~.,[]|");
    $temp2 = explode("~",$temp[0]);
    if (count($temp2) == 2 || substr($temp[0],0,4) == "RES_") {
      $temp3 = explode(",",$temp[1]);
      if (count($temp3) == 2) {
        if ($curcat != "") $BSFormat[$curcat][trim($temp[0])] = array(trim($temp3[0]),trim($temp3[1]));
      }
    }
  }
}

// Check if any stats were actually found.
if (count($BSFormat) == 0) {
  echo "No Main categories were specified in format.ini.<br>\nAt least one Main category is required by the BeardStat Web API.";
  exit();
}

// Now load and parse the listformat.ini file and put it into $BSListFormat.

$lformat = @file("listformat.ini");
if (!$lformat) die("Cannot load listformat.ini.<br>\nMake sure it's in the same directory as this script or in the include_path.");
foreach ($lformat as $key => $value) {
  $temp = strpos($value,";");
  if ($temp !== false) $value = substr($value,0,$temp);
  $value = trim($value);
  $temp = explode("=",$value);
  if (count($temp) == 2) {
    $temp[0] = al($temp[0],"0123456789_-+~.,[]|");
    $temp2 = explode("~",$temp[0]);
    if (count($temp2) == 2) {
      $temp3 = explode(",",$temp[1]);
      if (count($temp3) == 4) {
        $temp3[3] = strtolower(trim($temp3[3]));
        if ($temp3[3] != "asc") $temp3[3] = "desc";
        $BSListFormat[] = array(trim($temp[0]),trim($temp3[0]),trim($temp3[1]),trim($temp3[2]),$temp3[3]);
      }
    }
  }
}
// Alphabetize $BSListFormat by top list titles if the config says to do so.
// To note, the IDs (keys) will be reset when they are alphabetized. This may affect scripts that rely on knowing
// the top list order manually, but is generally not an issue. Hell, who's gonna even code a custom layout anyway, right? :-p
if (BS_CFG_ALPHABETIZE) {
  function mcomp($a,$b) {
    return strcasecmp($a[1],$b[1]);
  }
  usort($BSListFormat,"mcomp");
}

// Load the Excluded Players list if $include_all is false.

if (!BS_CFG_INCLUDEALL) {
  BS_load_excluded(BS_CFG_EXCLUDEFILE);
}

  /*******************************************************************************************/
 /**] End External Loading Section -------------------------------------------------------[**/
/*******************************************************************************************/










// Begin function declarations

function BS_version_string() {
  // Returns a string representing the current version and revision number for the current
  // version of the BeardStat Web API running.

  $temp = (BS_AUTHOR === BS_REVISIONAUTHOR) ? BS_DEVVERSION." ".BS_VERSION." R".BS_REVISION." by ".BS_AUTHOR: BS_DEVVERSION." ".BS_VERSION." by ".BS_AUTHOR." - R".BS_REVISION." by ".BS_REVISIONAUTHOR;
  return $temp;
}



function BS_total_players($time=-1) {
  // Returns the total number of players who have stats recorded in the database. If $time is specified, the
  // total number of players who have a stats~lastlogin stat >= to that are returned. Useful for checking
  // "What players have been online in the past 7 days" type of stuff. Best to use strtotime() or mktime() 
  // to convert a specific date to a timestamp. Also time() minus however many seconds you want to check for
  // would work.

  if ($time != -1) $time = floor(abs($time));
  $que = "select count(*) as total from ".BS_CFG_DBNAME.".".BS_CFG_TABLENAME." where category = \"stats\" && stat = \"lastlogin\" && value >= ".$time;
  $sel = mysql_query($que) or die("[BSTP-Q01] Error: <b>".mysql_error()."</b>");
  $temp = mysql_fetch_assoc($sel);
  return $temp['total'];
}



function BS_find_player($player="",$limit=9999999999,$force=false) {
  // Returns an array of possible name matches for $player. If only one player is found, or the $player
  // specified matches a name exactly, a string is returned with that player's name. If no players are found,
  // or $player isn't specified, false is returned. If $player is < 2 or > 16 characters in length, -1 will be
  // returned.

  // If $limit is specified, it will limit the returned values to that amount.

  // If $force is set to true, even if only a single player is found, an array containing only one entry is
  // returned. As well, even if $player matched a player name exactly, the full array containing all of the
  // names found is returned.

  $player = al($player,"0123456789_");
  if (strlen($player) < 2 || strlen($player) > 16) return -1;
  $player = str_replace("_","\\_",$player);
  if ($player == "") return false;
  $force = ($force != true) ? false: true;
  $limit = floor(abs($limit));
  if ($limit < 1) $limit = 1;

  $que = "select player from ".BS_CFG_DBNAME.".".BS_CFG_TABLENAME." where player like \"%".$player."%\" group by player order by player asc limit ".$limit;
  $sel = mysql_query($que) or die("[BSFP-Q01] Error: <b>".mysql_error()."</b>");
  if (mysql_num_rows($sel) == 0) return false;
  $res = array();
  while ($temp = mysql_fetch_assoc($sel)) {
    if (!$force && strtolower($player) == strtolower($temp['player'])) return $temp['player'];
    $res[] = $temp['player'];
  }
  if (!$force && count($res) == 1) $res = $res[0];
  return $res;
}



function BS_load_player($player="") {
  // Retrieves and stores a specified player's stats into memory.
  // Returns true upon success, and false if no player is found.

  global $BSLoaded;

  $player = al($player,"0123456789_");
  $que = "select * from ".BS_CFG_DBNAME.".".BS_CFG_TABLENAME." where player = \"".$player."\"";
  $sel = mysql_query($que) or die("[BSLP-Q01] Error: <b>".mysql_error()."</b>");
  if (mysql_num_rows($sel) != 0) {
    $BSLoaded['current_player'] = "";
    $BSLoaded['current_player_stats'] = array();
    while ($temp = mysql_fetch_assoc($sel)) {
      if ($BSLoaded['current_player'] == "") $BSLoaded['current_player'] = $temp['player'];
      $temp['category'] = strtolower($temp['category']);
      $temp['stat'] = strtolower($temp['stat']);
      if (!isset($BSLoaded['current_player_stats'][$temp['category']])) $BSLoaded['current_player_stats'][$temp['category']] = array();
      $BSLoaded['current_player_stats'][$temp['category']][$temp['stat']] = $temp['value'];
    }
    return true;
  }
  else return false;
}



function BS_purge_player() {
  // Purges the currently loaded player's stats.
  // This function will always return true.

  global $BSLoaded;

  $BSLoaded['current_player'] = "";
  $BSLoaded['current_player_stats'] = array();
  return true;
}



function BS_loaded_player() {
  // Returns the name of the player whose stats are currently loaded in $BSLoaded, or false if
  // no player is currently loaded.
  // This value is also generally available in $BSLoaded['current_player'], although it will be an empty
  // string if no player is loaded, instead of false.

  global $BSLoaded;

  return ($BSLoaded['current_player'] != "") ? $BSLoaded['current_player']: false;
}



function BS_load_excluded($file="") {
  // Checks for the existance of $file and loads it as the Excluded Players List if it exists.
  // Will remove all current Excluded Players before adding the new ones.

  global $BSExcluded,$BSExcluded_SQL;

  if ($file == "") $file = BS_CFG_EXCLUDEFILE;
  $lformat = @file($file);
  if ($lformat && $file != "") {
    BS_purge_excluded();
    foreach ($lformat as $key => $value) {
      $temp = str_replace(","," ",strtolower(al($value,"0123456789_, ")));
      if ($temp != "") {
        $temp2 = explode(" ",$temp);
        foreach ($temp2 as $key2 => $value2) {
          $value2 = substr(al($value2,"0123456789_"),0,16);
          if ($value2 != "" && strlen($value2) >= 2) {
            if ($BSExcluded_SQL != "") $BSExcluded_SQL .= " && ";
            $BSExcluded[] = $value2;
            $BSExcluded_SQL .= "player != \"".$value2."\"";
          }
        }
      }
    }
    if ($BSExcluded_SQL != "") $BSExcluded_SQL = " && (".$BSExcluded_SQL.")";
    return true;
  }
  return false;
}



function BS_is_player_excluded($player="") {
  // Returns true if the $player is on the excluded list, or false if they are not. This function does not
  // check if the $player specified actually exists, just if their name appears on the excluded list.
  // If $player is omitted or is an empty string, it will check the currently loaded player. If no player
  // is loaded, false is returned.

  global $BSExcluded;

  if ($player == "" && BS_loaded_player()) $player = BS_loaded_player();
  else if ($player == "") return false;
  $player = strtolower($player);
  if (in_array($player,$BSExcluded)) return true;
  return false;
}



function BS_purge_excluded() {
  // Purges the currently loaded Excluded list. This function will always return true.

  global $BSExcluded,$BSExcluded_SQL;

  $BSExcluded = array();
  $BSExcluded_SQL = "";
  return true;
}



function BS_next_category() {
  // This function will return the next main category for the loaded stats format, or false if there
  // are no more categories left.

  global $BSFormat;

  $temp = current($BSFormat);
  if (count($BSFormat) != 0 && $temp !== false) {
    $temp = key($BSFormat);
    next($BSFormat);
    return $temp;
  }
  else return false;
}



function BS_current_category() {
  // This function will return the current main category that the pointer is on without increasing it to the
  // next one, or if there's no more categories, it will return the last category.

  global $BSFormat;

  $temp = current($BSFormat);
  if ($temp !== false) return key($BSFormat);
  else {
    end($BSFormat);
    $temp = key($BSFormat);
    next($BSFormat);
    return $temp;
  }
}



function BS_reset_category() {
  // Resets the pointer for the categories to the first element, then returns it.

  global $BSFormat;

  reset($BSFormat);
  return key($BSFormat);
}



function BS_next_stat($category="") {
  // Returns the next stat in $BSFormat for the main $category specified, or false if no more stats in that
  // category exist or the category specified doesn't exist. This will also return false if no player's
  // stats are currently loaded.

  // Refer to BS_current_stat() for information about the returned values.

  global $BSFormat;

  if ($category == "") $category = BS_current_category();
  if (!BS_loaded_player() || !isset($BSFormat[$category])) return false;
  $temp = current($BSFormat[$category]);
  if ($temp !== false) {
    $temp = BS_current_stat($category);
    next($BSFormat[$category]);
    return $temp;
  }
  else return false;
}



function BS_current_stat($category="") {
  // Returns the current stat in $BSFormat for the main $category specified, or false if the category specified
  // doesn't exist or if no player's stats are currently loaded.

  // The returned values are in an array:
  // [0] = Data type. Will be 0 for a stat entry, or 1 for a sub-category.
  // [1] = Name of stat if it's a stat entry, or sub-category title if it's a sub-category.
  // [2] = Stat value - processed for proper display.
  // [3] = Raw stat value.
  // [4] = The stat category used to get the stat value.
  // [5] = The raw list of "+"-separated stats used to get the stat value.
  // [6] = An array of the stats used to get the stat value. Basically an explode()'ed version of [5].

  // If the entry is a sub-category, then all the values not-relevant to it will be empty strings, or arrays
  // with no values. If the category used is a "RES_" value to be processed, values [4], [5] and [6] may be
  // empty strings/arrays.

  global $BSFormat,$BSLoaded;

  if ($category == "") $category = BS_current_category();
  if (!BS_loaded_player() || !isset($BSFormat[$category])) return false;
  $temp = current($BSFormat[$category]);
  if ($temp === false) {
    $temp = end($BSFormat[$category]);
    $temp2 = explode("~",key($BSFormat[$category]));
    next($BSFormat[$category]);
  }
  else $temp2 = explode("~",key($BSFormat[$category]));
  $res = array(0,"","","","","",array());
  $tempr = 0;
  if (count($temp) == 1) {
    $res[0] = 1;
    $res[1] = $temp[0];
  }
  else if (substr($temp2[0],0,4) == "RES_") {
    if ($temp2[0] == "RES_PlayTime" && isset($BSLoaded['current_player_stats']['stats']['playedfor'])) {
      $res[3] = $BSLoaded['current_player_stats']['stats']['playedfor'];
      $res[2] = gettimeformat($res[3],BS_CFG_TFORMAT);
      $res[4] = "stats";
      $res[5] = "playedfor";
      $res[6] = array("playedfor");
    }
    else if ($temp2[0] == "RES_FirstLogin" && isset($BSLoaded['current_player_stats']['stats']['firstlogin'])) {
      $res[3] = $BSLoaded['current_player_stats']['stats']['firstlogin'];
      $res[2] = date(BS_CFG_DFORMAT,$res[3]);
      $res[4] = "stats";
      $res[5] = "firstlogin";
      $res[6] = array("firstlogin");
    }
    else if ($temp2[0] == "RES_LastLogin" && isset($BSLoaded['current_player_stats']['stats']['lastlogin'])) {
      $res[3] = $BSLoaded['current_player_stats']['stats']['lastlogin'];
      $res[2] = date(BS_CFG_DFORMAT,$res[3]);
      $res[4] = "stats";
      $res[5] = "lastlogin";
      $res[6] = array("lastlogin");
    }
    else if ($temp2[0] == "RES_LastSession" && isset($BSLoaded['current_player_stats']['stats']['lastlogin']) && isset($BSLoaded['current_player_stats']['stats']['lastlogout'])) {
      $temp3 = ($BSLoaded['current_player_stats']['stats']['lastlogout']-$BSLoaded['current_player_stats']['stats']['lastlogin']);
      if ($temp3 < 1) {
        $res[2] = BS_CFG_ONLINETEXT;
        $res[3] = 0;
      }
      else {
        $res[2] = gettimeformat($temp3,BS_CFG_TFORMAT);
        $res[3] = $temp3;
      }
    }
    else if ($temp2[0] == "RES_ChatAverage" && isset($BSLoaded['current_player_stats']['stats']['chat']) && isset($BSLoaded['current_player_stats']['stats']['chatletters'])) {
      $res[2] = round($BSLoaded['current_player_stats']['stats']['chatletters']/$BSLoaded['current_player_stats']['stats']['chat'],2);
      $res[3] = $res[2];
    }
    else {
      $res[2] = 0;
      $res[3] = 0;
    }
    $res[1] = $temp[0];
    $res[2] = str_replace("%",$res[2],$temp[1]);
  }
  else {
    $temp3 = explode("+",$temp2[1]);
    foreach ($temp3 as $key => $value) {
      if (isset($BSLoaded['current_player_stats'][$temp2[0]][$value])) $tempr = ($tempr+$BSLoaded['current_player_stats'][$temp2[0]][$value]);
    }
    $res[1] = $temp[0];
    $res[2] = str_replace("%",number_format($tempr,0,".",BS_CFG_TSEPARATOR),$temp[1]);
    $res[3] = $tempr;
    $res[4] = $temp2[0];
    $res[5] = $temp2[1];
    $res[6] = $temp3;
  }
  return $res;
}



function BS_reset_stat($category="") {
  // Resets the pointer for the current main category back to the first entry and returns it. Returns false if
  // the $category doesn't exist, or no player's stats are loaded.

  global $BSFormat,$BSLoaded;

  if ($category == "") $category = BS_current_category();
  if (!BS_loaded_player() || !isset($BSFormat[$category])) return false;
  reset($BSLoaded[$category]);
  return BS_current_stat($category);
}



function BS_reset_all_stats() {
  // Resets all pointers for every currently loaded main category. Returns true upon success, or false if there's no
  // stats currently loaded for a player. This will also reset the position of the categories.

  global $BSFormat,$BSLoaded;

  if (!BS_loaded_player()) return false;
  foreach ($BSFormat as $key => $value) {
    reset($BSFormat[$key]);
  }
  reset($BSFormat);
  return true;
}



function BS_stat_position($category="",$stat="",$player="",$aord="desc") {
  // Checks to see what position a given player's stat is relative to other players. This function queries
  // the database everytime it's called and therefore may become memory-intensive with scripts designed to
  // check this for every stat entry.

  // If $player is omitted, is an empty string or is equal to the currently loaded player's name, it will use
  // the currently loaded player's stats, unless there is no loaded player, which it will then return false.

  // If the player specified is on the excluded list, -1 will be returned.

  // Upon success, this function will return a number between 1 (first) and the number of players in the database.
  // If the total number of stats specified in $stat is equal to 0, this function will return -1 unless "asc" is
  // specified for $aord. In that case it will return the position as expected.
  // $stat should be an array of stats to be added together. If $stat is not an array, it will be converted into
  // an array with the assumption that its entries are a "+"-separated list.
  // $aord should either be "asc" for ascending comparison, or "desc" for descending. If it's anything else,
  // it will default to "desc."

  // False will be returned if the $category or stats in $stat aren't found. In the rare-ish occurrence that no
  // players have recorded any of a specific stat type that would normally be recorded, this will still return
  // false. This may be scripted as an error when it "really" isn't, but shouldn't affect normal scripts.

  global $BSLoaded,$BSExcluded_SQL;

  $category = al(strtolower($category),"0123456789_-+~.,[]|");
  $player = al(strtolower($player),"0123456789_");
  if ($category == "" || $stat == "") return false;
  if (!is_array($stat)) $stat = explode("+",$stat);
  if ($player == "" && BS_loaded_player()) $player = BS_loaded_player();
  else if ($player == "") return false;
  $temp = 0;
  if (BS_is_player_excluded($player)) return -1;
  $qins = "";
  foreach ($stat as $key => $value) {
    if ($qins != "") $qins .= " || ";
    $value = str_replace("\"","",str_replace("\\","",$value));
    if ($value != "") $qins .= "stat = \"".$value."\"";
    if ($player == strtolower(BS_loaded_player())) {
      if (isset($BSLoaded['current_player_stats'][$category][$value])) $temp += $BSLoaded['current_player_stats'][$category][$value];
    }
  }
  if ($qins == "") return false;
  if ($player != strtolower(BS_loaded_player())) {
    $que = "select sum(value) as total from ".BS_CFG_DBNAME.".".BS_CFG_TABLENAME." where player = \"".$player."\" && category = \"".$category."\" && (".$qins.")";
    $sel = mysql_query($que) or die("[BSStP-Q01] Error: <b>".mysql_error()."</b>");
    if (mysql_num_rows($sel) == 0) $temp = -1;
    else {
      $temp = mysql_fetch_assoc($sel);
      $temp = ($temp['total']);
    }
  }
  if ($temp == 0) return -1;
  $aord = (strtolower($aord) == "asc") ? "asc": "desc";
  $qins2 = ($aord == "asc") ? "<": ">";
  $que = "select player from ".BS_CFG_DBNAME.".".BS_CFG_TABLENAME." where category = \"".$category."\" && (".$qins.")".$BSExcluded_SQL." group by player having sum(value) ".$qins2." ".$temp."";
  $sel = mysql_query($que) or die("[BSStP-Q02] Error: <b>".mysql_error()."</b>");
  $temp = (mysql_num_rows($sel)+1);
  return $temp;
}



function BS_next_list() {
  // Returns the next entry for the top stats list or false if there's no more entries left. The returned
  // values will be in an array. Refer to BS_current_list() for details on the returned values.

  global $BSListFormat;

  $temp = current($BSListFormat);
  if (count($BSListFormat) != 0 && $temp !== false) {
    $temp = BS_current_list();
    next($BSListFormat);
    return $temp;
  }
  else return false;
}



function BS_current_list($specific=-1) {
  // Returns the current entry for the top stats list without increasing the pointer. If a $specific ID is specified,
  // then the entry with that ID will be returned, unless that ID does not exist, then just the current list is returned.
  // The returned values will be in an array:

  // [0] = The loaded ID of the top list. Will be an integer.
  // [1] = The name of the top list.
  // [2] = The description of the top list.
  // [3] = "asc" or "desc" setting for the top list.
  // [4] = The stat category that will be used to retrieve the top list.
  // [5] = The raw list of "+"-separated stats that will be used to get the top list.
  // [6] = An array of the stats that will be used to get the top list. Basically an explode()'ed version of [5].

  global $BSListFormat;

  $temp = ($specific != -1 && isset($BSListFormat[$specific])) ? $BSListFormat[$specific]: current($BSListFormat);
  if (count($BSListFormat) == 0) return false;
  if ($temp === false) {
    $temp = end($BSListFormat);
    $temp2 = key($BSListFormat);
    next($BSListFormat);
  }
  else $temp2 = ($specific != -1 && isset($BSListFormat[$specific])) ? $specific: key($BSListFormat);
  $temp3 = explode("~",$temp[0]);
  $temp4 = explode("+",$temp3[1]);
  $res = array($temp2,$temp[1],$temp[2],$temp[4],$temp3[0],$temp3[1],$temp4);
  return $res;
}



function BS_reset_list() {
  // Resets the pointer for the top lists and returns the first value.

  global $BSListFormat;

  $temp = reset($BSListFormat);
  if ($temp === false) return false;
  else return BS_current_list();
}



function BS_search_list($category="",$stat="") {
  // Searches for a specific top list entry that matches the $category specified, and contains
  // at least one of the stats specified in $stat, which should be an array. If $stat is not an array,
  // it will be converted to an array with the assumption its entries are a "+"-separated list.
  // If no entries are found, false is returned.

  // If a Top List is found, an array of information about it will be returned. Refer to
  // BS_current_list() for which array entries will contain which data.

  global $BSListFormat;

  $category = al(strtolower($category),"0123456789_-+~.,[]|");
  if ($category == "" || $stat == "") return false;
  if (!is_array($stat)) {
    $stat = explode("+",$stat);
  }
  $x = count($BSListFormat);
  for ($i=0;$i<$x;$i++) {
    $temp = explode("~",$BSListFormat[$i][0]);
    if ($temp[0] == $category) {
      $temp[1] = "+".$temp[1]."+";
      foreach ($stat as $value) {
        if (stristr($temp[1],"+".strtolower($value)."+") !== false) {
          return BS_current_list($i);
        }
      }
    }
  }
  return false;
}



function BS_load_top_list($catorid="",$top=0,$stat="",$format="%",$aord="desc") {
  // Loads a top list into memory. If $catorid is an integer and is a valid ID for a top list as loaded
  // in $BSListFormat, then it will load its data from there. Otherwise, it assumes it's a category, and
  // expects $stat to be an array of stats to be added together. If $stat is not an array, it will be
  // converted to one while assuming that the value is a "+"-separated list of stats.

  // $top will limit the list to the highest (or lowest for "asc") players with the stats specified. If
  // it is 0, an empty string, or omitted, it will use the value in $default_top_list as default.

  // The $format is the string that will be used for the value, with a "%" being replaced with the
  // formatted value. If omitted or an empty string, it will be set to just "%."

  // $aord should either be "asc" for ascending comparison, or "desc" for descending. If it's anything else,
  // it will default to "desc."

  // Upon success, true will be returned. False will be returned if $catorid or $stat is not specified.

  // NOTE: In the case of specifying $catorid with an ID of a loaded top list, you should not specify
  //       any value for $stat, $format or $aord, as they will be filled in automatically.

  global $BSListFormat,$BSLoaded;

  $tid = "";
  if (isset($BSListFormat[$catorid])) {
    $tid = $catorid;
    $temp = explode("~",$BSListFormat[$catorid][0]);
    $stat = explode("+",$temp[1]);
    $format = $BSListFormat[$catorid][3];
    $aord = $BSListFormat[$catorid][4];
    $catorid = $temp[0];
  }

  $top = floor(abs($top));
  if ($top == 0) $top = BS_CFG_DEFAULTTOP;

  $catorid = str_replace("\"","",str_replace("\\","",$catorid));
  if ($catorid == "" || $stat == "") return false;
  if (!is_array($stat)) $stat = explode("+",$stat);
  if ($format == "") $format = "%";
  $aord = ($aord == "asc") ? "asc": "desc";

  $qins = "";
  foreach ($stat as $key => $value) {
    if ($qins != "") $qins .= " || ";
    $value = str_replace("\"","",str_replace("\\","",$value));
    if ($value != "") $qins .= "stat = \"".$value."\"";
  }
  if ($qins == "") return false;
  $que = "select *,sum(value) as total from ".BS_CFG_DBNAME.".".BS_CFG_TABLENAME." where category = \"".$catorid."\" && (".$qins.") group by player order by total ".$aord." limit ".$top;
  $sel = mysql_query($que) or die("[BSLTL-Q01] Error: <b>".mysql_error()."</b>");
  $BSLoaded['current_top_results'] = mysql_num_rows($sel);
  $BSLoaded['current_top_eresults'] = $top;
  $BSLoaded['current_top_id'] = $tid;
  $BSLoaded['current_top_category'] = $catorid;
  $BSLoaded['current_top_stat'] = implode("+",$stat);
  $BSLoaded['current_top_format'] = $format;
  $BSLoaded['current_top_aord'] = $aord;
  $BSLoaded['current_top_list'] = array();
  $count = 1;
  while ($temp = mysql_fetch_assoc($sel)) {
    $temp2 = $temp['total'];
    if ($catorid == "stats" && count($stat) == 1 && $stat[0] == "playedfor") $temp2 = gettimeformat($temp2,BS_CFG_TFORMAT);
    else if ($catorid == "stats" && count($stat) == 1 && ($stat[0] == "firstlogin" || $stat[0] == "lastlogin" || $stat[0] == "lastlogout")) $temp2 = date(BS_CFG_DFORMAT,$temp2);
    else $temp2 = number_format($temp2,0,".",BS_CFG_TSEPARATOR);
    $BSLoaded['current_top_list'][] = array($count,$temp['player'],str_replace("%",$temp2,$format),$temp['total']);
    $count++;
  }
  return true;
}



function BS_purge_top_list() {
  // Purges the currently loaded top list.
  // This function always returns true.

  global $BSLoaded;

  $BSLoaded['current_top_results'] = -1;
  $BSLoaded['current_top_eresults'] = -1;
  $BSLoaded['current_top_id'] = "";
  $BSLoaded['current_top_category'] = "";
  $BSLoaded['current_top_stat'] = "";
  $BSLoaded['current_top_format'] = "%";
  $BSLoaded['current_top_aord'] = "desc";
  $BSLoaded['current_top_list'] = array();
  return true;
}



function BS_loaded_top_list() {
  // Returns an array of data if a top list is currently loaded, or false if not.
  // The array values consist of:

  // [0] = The number of actual results returned.
  // [1] = The number of results EXPECTED when the list was loaded.
  // [2] = The top list ID, if used, when the list was loaded, or a blank string if an ID wasn't used to load the results.
  // [3] = The category specified or loaded from an ID when the list was loaded.
  // [4] = A "+"-separated list of stats used to load the top list.
  // [5] = The format used when the top list was loaded.
  // [6] = "asc" or "desc" specified when the top list was loaded.

  global $BSLoaded;

  if ($BSLoaded['current_top_results'] < 0) return false;
  else return array($BSLoaded['current_top_results'],$BSLoaded['current_top_eresults'],$BSLoaded['current_top_id'],$BSLoaded['current_top_category'],$BSLoaded['current_top_stat'],$BSLoaded['current_top_format'],$BSLoaded['current_top_aord']);
}



function BS_next_top_list() {
  // Returns an array containing data for the next entry in the loaded top list, or false if no top
  // list is loaded. Refer to BS_current_top_list() for details on what array values are returned.

  global $BSLoaded;

  if ($BSLoaded['current_top_results'] == -1) return false;
  $temp = BS_current_top_list();
  next($BSLoaded['current_top_list']);
  return $temp;
}



function BS_current_top_list($specific=-1) {
  // Returns an array containing data for the current entry in the loaded top list, or false if no top
  // list is loaded. If $specific is set, and is 1 or higher, the data for that position will be returned.
  // The array values will be:

  // [0] = Current position for the loaded stat. First place will be 1, second is 2, etc.
  // [1] = Name of player.
  // [2] = Formatted and processed value.
  // [3] = Unformatted value.

  global $BSLoaded;

  if ($BSLoaded['current_top_results'] == -1) return false;
  $specific = ($specific != -1 && is_numeric($specific)) ? (floor(abs($specific))-1): -1;
  $temp = ($specific != -1 && isset($BSLoaded['current_top_list'][$specific])) ? $BSLoaded['current_top_list'][$specific]: current($BSLoaded['current_top_list']);
  return $temp;
}



function BS_reset_top_list() {
  // Resets the pointer for the currently loaded top list, and returns the first value as an array. If
  // no top list is loaded, returns false. Refer to BS_current_top_list() for details on what array
  // values are returned.

  global $BSLoaded;

  if ($BSLoaded['current_top_results'] == -1) return false;
  reset($BSLoaded['current_top_list']);
  return BS_current_top_list();
}










function gettimeformat($time=0,$format=2) {

  /*****[ © Rich Innovations ]*******************************************************************************

	Formats the number of seconds given with $time into a textual representation of the time using one of
	the built in $formats.

	Required Functions: NONE


	- $time		= An integer representing any number of seconds.
	- $format	= From 0-3, will use one of the internal formats. This defaults to 2, as it's the
			  most common and proper.
			  0 example: 1w 12d 3h 55m 3s
			  1 example: 1 week, 12 days, 3 hrs, 55 mins, 3 secs
			  2 example: 1 Week, 12 Days, 3 Hours, 55 Minutes, 3 Seconds
			  3 example: Weeks: 1 - Days: 12 - Hours: 3 - Mins: 55 - Secs: 3

  **********************************************************************************************************/

  $time = floor(abs($time));
  $tm = array(0,0,0,0,0);
  $tf = array();
  $tf[0] = array("!w","!d","!h","!m","!s"," ");
  $tf[1] = array("! week%","! day%","! hr%","! min%","! sec%",", ");
  $tf[2] = array("! Week%","! Day%","! Hour%","! Minute%","! Second%",", ");
  $tf[3] = array("Weeks: !","Days: !","Hours: !","Mins: !","Secs: !"," - ");
  if (!isset($tf[$format])) $format = 2;
  $tm[0] = floor($time/604800);
  $tm[1] = floor(($time-($tm[0]*604800))/86400);
  $tm[2] = floor(($time-($tm[0]*604800)-($tm[1]*86400))/3600);
  $tm[3] = floor(($time-($tm[0]*604800)-($tm[1]*86400)-($tm[2]*3600))/60);
  $tm[4] = floor($time-($tm[0]*604800)-($tm[1]*86400)-($tm[2]*3600)-($tm[3]*60));
  $res = "";
  foreach ($tm as $key => $value) {
    if ($value != 0) {
      if ($res != "") $res .= $tf[$format][5];
      $temp = str_replace("!",number_format($value),$tf[$format][$key]);
      $temp = ($value != 1) ? str_replace("%","s",$temp): str_replace("%","",$temp);
      $res .= $temp;
    }
  }
  if ($res == "") $res = str_replace("!","0",str_replace("%","s",$tf[$format][4]));
  return $res;
}



function al($a="") {

  /*****[ © Rich Innovations ]*******************************************************************************

	This strips all non-alphabetic characters from $a. If you want to keep extra characters, then include
	a second parameter with each character you want kept.

	Required Functions: NONE


	- $a		= String to be stripped.

  **********************************************************************************************************/

  $extra = "";
  if (func_num_args() >= 2) $extra = func_get_arg(1);
  $temp = "";
  $aln = "abcdefghijklmnopqrstuvwxyz".$extra;
  for ($b=0;$b<strlen($a);$b++) {
    $temp2 = substr($a,$b,1);
    if (stristr($aln,$temp2)) {
      $temp .= $temp2;
    }
  }
  return $temp;
}



function suffix($a=1) {

  /*****[ © Rich Innovations - Modified for the BeardStat API ]**********************************************

	Gets the textual suffix for the number $a

	Required Functions: NONE


	- $a		= Number to be used.

  **********************************************************************************************************/

  if (!is_numeric($a)) return $a;
  $a = abs(floor($a));
  $ac = array("th","st","nd","rd");
  $b = $ac[0];
  if (isset($ac[substr($a,-1)]) && (strlen($a) < 2 || substr($a,-2,1) != 1)) $b = $ac[substr($a,-1)];
  return number_format($a,0,".",BS_CFG_TSEPARATOR).$b;
}


















?>