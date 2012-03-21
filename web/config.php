<?php

/*********************************************/
/*.------------------------------------------'
/*¦  Connection Information.
/*'-------------------------------------------------------------------------------------------.
/*********************************************************************************************/

$database_host = "localhost";
$database_user = "root";
$database_pass = "";

$database_name = "";

/*]- End Connection Information -[************************************************************/



/*********************************************/
/*.------------------------------------------'
/*¦  General Settings
/*'-------------------------------------------------------------------------------------------.
/*********************************************************************************************/

$time_format = 2;                  // Refer to readme.txt for information on available time formats and examples.
$date_format = "F jS, Y. g:i A";   // A string that is passed directly to the date() function for formatting date-related stats.
$time_online = "Online Currently"; // The text that is used if a player is online currently for RES_LastSession.
$thousands_separator = ",";        // The character used between grouped thousands.
$alphabetize_list = false;         // If this is set to true, the listformat.ini file will be sorted by the top list title, rather than natural order.
$default_top_list = 20;            // This value will be used as default if no value is specified when loading a top list.
$exclude_file = "exclude.txt";     // The file location of the Excluded Players List. Refer to readme.txt for information on exclusions.
$include_all = false;              // True/false as to whether to INCLUDE the players listed in the exclude.txt file. Refer to readme.txt for information on exclusions.

/*]- End General Settings -[******************************************************************/

?>