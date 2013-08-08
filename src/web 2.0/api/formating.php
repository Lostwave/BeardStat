<?php
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

?>