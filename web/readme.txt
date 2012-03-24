+---------------------------------------------------------------------------------------------+
¦ BeardStat Web API Beta v0.1 r1 for BeardStat v0.4 (grapefruit)                              ¦
+---------------------------------------------------------------------------------------------+
¦ Coded by Richard "Furdabip" Moyer.                                                          ¦
¦ Last Updated: March 20th, 2012                                                              ¦
¦ BeardStat website: http://dev.bukkit.org/server-mods/beardstat/                             ¦
+---------------------------------------------------------------------------------------------+
¦ Documentation for Beta v0.1 r1                                                              ¦
+---------------------------------------------------------------------------------------------+
¦ Written by Richard "Furdabip" Moyer.                                                        ¦
¦ http://www.furdabip.com/                                                                    ¦
+---------------------------------------------------------------------------------------------+

+------------+
¦ Navigation ¦
+------------+

 General Information
 ===================
     I. Changelog & Version History ....... [VERSION]
    II. Introduction & Getting Started .... [INTRO]
   III. Configuration Settings ............ [CONFIG]
    IV. External File Formats ............. [FORMAT]
     V. Excluded Players .................. [EXCLUDED]


 Using the API
 =============
    VI. Features & Information ............ [FEATURES]
   VII. Flow Chart ........................ [FLOW]
  VIII. Function List ..................... [FUNCTIONS]
         a. General Functions ............. [FUNCTIONS_GENERAL]
         b. Player Functions .............. [FUNCTIONS_PLAYER]
         c. Category Functions ............ [FUNCTIONS_CAT]
         d. Statistic Functions ........... [FUNCTIONS_STAT]
         e. Top Lists Functions ........... [FUNCTIONS_TOP]

    IX. Variable References ............... [VARIABLES]
     X. Database Entries Reference ........ [DATABASE]
         a. Stat Glossary ................. [GLOSSARY]


 License
 =======
    XI. License & Copyright Information ... [LICENSE]





+---------------------------------------------------------------------------------------------+
¦    I. Changelog & Version History                                                 [VERSION] ¦
+---------------------------------------------------------------------------------------------+

 v0.1 r1 Beta [March 20th, 2012]
 ===============================
   - Release.










+---------------------------------------------------------------------------------------------+
¦   II. Introduction & Getting Started                                                [INTRO] ¦
+---------------------------------------------------------------------------------------------+

 The BeardStat Web API is designed to be a way for everyone from the newbie PHP programmer to
 the veteran coder to display information about your Minecraft players' stats on a website.
 The API can be integrated with most any layout or design. Either by default or custom coded.
 If the API doesn't do something you think it should, then you can dive right in and alter the
 files that make it all work.

 If you don't want to code anything at all, then don't worry! The default layout is gray and
 uses gradients to make it pleasing to the eyes. It should integrate with most website
 designs well. Want to put just a little effort in? You can easily change the color scheme by
 changing the CSS. Don't have image editing skills enough to make some gradients? No problem!
 You can use Dynamic Drive's gradient image maker; http://tools.dynamicdrive.com/gradient/

 If you have any suggestions on features that should be added, comments, concerns, or wish to
 translate the default layout into a different language, then feel free to contact us on the
 BeardStat website (listed at the top of this readme).


 Getting Started
 ===============
   You've probably already unzipped all the files, and like a good little boy/girl are reading
   this readme for information. That accomplishes Step 1!

   Step 2 would be to put/upload the unzipped files into the directory you want your
   statistics to be in. The index.php file is the layout and will rely on the remaining files
   being in the same directory. It's named index.php by default to ensure that the people who
   use the most "default of the default" settings won't have their directories list the files
   in them. It is safe to rename it to whatever you want. If you are including the layout into
   your website's design/layout, it should be safe to just outright include() or require() the
   layout file as-is. There may need to be some adjustments to make it look the way you want.

   Step 3 would be configuration settings. The config.php file contains all the "basic"
   settings you need to make the default layout work. Each setting is described in detail
   below in the Configuration Settings section. Basically you'll want to set the database
   settings to work with your/your server's MySQL installation. The default settings should
   be fine enough to make it work after that.

   Step 4, should you choose to accept it, may be to integrate the default layout into your
   website better or code an entirely new layout. Helping you do that is what the majority of
   this readme is for. I'm sure telling you to "read the entire readme" won't help you find
   specific information you need to get started, so I recommend you start by reading the
   Flow Chart section. It gives basic information on how the API is expected to work. The
   Function List section lists all the functions in the BeardStat Web API as well as
   describes what they do, so should definitely be read. For very basic changes, though, you
   might want to start out changing around the format.ini and listformat.ini files. They
   contain the raw data that layout scripts (should) use. Information on the format and how
   they work is in the External File Formats section.


 Included Files and What They Are For
 ====================================
   img               - The images the default layout uses. They are gradients used to make the
     (folder)          layout more polished.

   other layouts     - Any additional default layouts that may have been created for use. They
     (folder)          will contain information about them in their source code (like usual).

   beardstat.php     - The API file. Includes all the functions needed to make it work.
                       Generally you shouldn't edit this just in case there are future
                       versions of the API. It will make upgrading easier. If there are
                       features you think should be added, please contact us if possible!

   config.php        - The configuration file.

   format.ini        - The format for the Statistics results used by the default layout
                       script and hopefully used by custom layouts.

   index.php         - The default layout file. Feel free to edit or rename this as needed.

   listformat.ini    - The format used for Top Lists in the layout.










+---------------------------------------------------------------------------------------------+
¦  III. Configuration Settings                                                       [CONFIG] ¦
+---------------------------------------------------------------------------------------------+

 Configuration settings are saved in config.php. Unless coded to accept a different filename,
 you should not rename config.php to anything else. All of these settings get unset after they
 are loaded and used. Refer to Variable References for information on which variables are kept
 and what they are set to.


 Database Connection Settings
 ============================
   $database_host: This should be the host of your MySQL server.

   $database_user: This should be the username you use to connect to your MySQL server.

   $database_pass: This should be the password you use to connect to your MySQL server.

   $database_name: This should be the name of the database. The default database is "stats."


 General Settings
 ================
   $time_format: The BeardStat Web API uses a function to display the amount of time passed
                 for certain statistics. This function comes with 4 built-in formats.
                 $time_format should be 0, 1, 2, or 3. Any other value and it will default
                 to 2. Examples that each format may return:

                 0: 1w 12d 3h 55m 1s
                 1: 1 week, 12 days, 3 hrs, 55 mins, 1 sec
                 2: 1 Week, 12 Days, 3 Hours, 55 Minutes, 1 Second
                 3: Weeks: 1 - Days: 12 - Hours: 3 - Mins: 55 - Secs: 1

                 Default: 2

   $date_format: This string will be passed as-is to the PHP date() function when formatting
                 date-related statistics. Refer to the date() function's help page on php.net
                 for specific information: http://php.net/manual/en/function.date.php

                 Default: "F jS, Y. g:i A"

   $time_online: This is the text that is displayed when calculating the Last Session length
                 of a player that is online currently.

                 Default: "Online Currently"

   $thousands_separator: The character that is used to separate grouped thousands for stats.
                         Example: "," would be 9,999,999 whereas " " would be 9 999 999.

                         Default: ","

   $alphabetize_list: If this is set to TRUE, then the Top List format data will be sorted
                      alphabetically according to the title of each entry when it is loaded.
                      Refer to the External File Formats section for information on how the
                      Top List Format works, and the Features & Information section for
                      information on just what a Top List even is.

                      Default: false

   $default_top_list: This is the number of results returned for a Top List. This will be
                      "Top [X] Coal Miners" as an example. Refer to the Features & Information
                      section for information on what Top Lists are.

                      Default: 20

   $exclude_file: This is the file location for the Excluded Players List. The file should be
                  plain text and readable by PHP. Refer to the Excluded Players section for
                  information on what excluded players are.

                  Default: "exclude.txt"

   $include_all: If this is set to TRUE, then all players specified in the Excluded Players
                 List will be included in the Top Lists. This basically allows you to override
                 the excluded players without having to erase and refill the excluded list.
                 Refer to the Excluded Players section for information on what excluded
                 players are.

                 Default: false










+---------------------------------------------------------------------------------------------+
¦   IV. External File Formats                                                        [FORMAT] ¦
+---------------------------------------------------------------------------------------------+

 The BeardStat Web API uses /kind of/ an .ini file format to define what statistics to display
 on the web page. Because of the support of main and sub-categories, a proper .ini file would
 not work. The files are still .ini, but will not parse properly if you tried to edit them
 with anything other than a basic text editor.

 If you are just starting out editing the default statistics page, here is a good place to
 change some things. No programming is needed.


 Statistics Format File: format.ini
 ======================
   The format for each main category is as follows:

     [Name of Category]

   The category must be surrounded by [square brackets] and every statistic must be part of a
   main category. All statistics defined before a main category is defined will be ignored. To
   change to a new main category, just define a new one.

   You can define sub-categories to give further description to a main category by just typing
   it without any square brackets or "=" sign. The parser should recognize it. An example
   of using a sub-category would be:

     [Blocks]
  -> Block Statistics
     stats~totalblockcreate=Total Blocks Placed,%
     stats~totalblockdestroy=Total Blocks Destroyed,%
  -> Blocks Mined/Destroyed
     blockdestroy~coalore=Coal,%
     ...

   The format for the statistics is as follows:

     Stat Category ~ Stats + Separate + By + A + Plus + Sign = Description of Stat , Format

 - The Stat Category is the category that is set by BeardStat.
 - The Stats consist of "+"-separated stats, as set by BeardStat that will be added together
     to get the final result for displaying.
 - The Description is the text that should appear to describe the stat on the web page.
 - The Format is a string that will have the special character "%" replaced with the processed
     result of the stat.

   As a working example:

     blockdestroy~dirt+grass=Dirt,%

   "blockdestroy" is the category for when players destroy blocks, "dirt" and "grass" blocks
   will be added together to get the final result, "Dirt" will be used to describe the
   statistic and "%" will be replaced by the final result when it is returned.

   You can use any number of stats to be added together as long as they are part of the same
   category. Another, bigger example would be:

     Tools Obtained
     itempickup~woodpickaxe+woodspade+woodaxe=Wooden Tools,%

   This would display, in a single result, how many Wooden Tools a player has picked up
   instead of display each result individually.

   You don't need to always specify multiple stats. If you don't include any "+" signs, then
   just the one stat specified will work by itself; "blockdestroy~log=Wood,%" as an example.


   RES_ Keywords
   =============
   In addition to normal categories and stats, you can use special pre-defined keywords that
   will be parsed and/or calculated by the API. These keywords start with RES_ and each one
   will load the data it requires and calculate whatever results are needed. You would use
   these keywords instead of the category~stat entry. As an example:

     RES_PlayTime=Play Time,%

   For RES_PlayTime, it returns a string with how many weeks, days, hours, minutes and seconds
   the loaded player has played for. Below are all the special keywords and which data they
   calculate out for you;

   - RES_PlayTime:    Calculates how many weeks, days, hours, minutes and/or seconds the
                      loaded player has been playing for.

   - RES_FirstLogin:  Returns a date() formatted string for the first time the loaded player
                      logged in.

   - RES_LastLogin:   Returns a date() formatted string for the last time the loaded player
                      was seen online.

   - RES_LastSession: Returns how long the loaded player played for the last time they were
                      online in weeks, hours, days, minutes and/or seconds.

   - RES_ChatAverage: Returns the average number of characters the loaded player has typed per
                      chat message they've sent.


 Top Lists Format File: listformat.ini
 =====================
   The entries for a Top List don't need a Main Category like the Statistics format, so you
   don't need to define any. However, you can define some if for some reason you really want
   to. The parser will just ignore them.

   The format is generally the same, but has more entries:

     Stat Category ~ Stats = Top List Title , Top List Description , Format , Sort Order

 - The Stat Category is the same as the Statistics Format. It's the category set by BeardStat.
 - The Stats are the same "+"-separated list of stats as defined by BeardStat. They will be
     added together and that total will be compared when loading a Top List.
 - The Top List Title is the text used to define the Top Stats. It will generally be refered
     to as "Top X [Top List Title]." So "Ghast Slayers" is a good title for most Ghast kills.
 - The Top List Description is text that isn't necessary, but will describe what the Title
     is referring to. For vague Titles like "Music Lovers," a good description might be
     "Most Music Discs obtained."
 - The Format is the same as the Statistics Format one, where "%" is replaced by the result
     when a Top List is loaded.
 - The Sort Order should be "asc" for statistics that are "the least" of something, and "desc"
     for "the most" of something. Generally statistics are "Who has the most [something]," so
     only in special circumstances should this be set to asc. If this value is anything other
     than asc or desc, it will default to desc.

   A working example of a Top List entry:

     stats~playedfor=Played Time,Longest time played on the server.,%,desc

   This would list the top X players who have played on the server the longest.










+---------------------------------------------------------------------------------------------+
¦    V. Excluded Players                                                           [EXCLUDED] ¦
+---------------------------------------------------------------------------------------------+

 You can specify a list of players to exclude from the Top Lists when calculating positions.
 This exists so you can eliminate admins, ops, or whomever else that may be able to spawn
 items or otherwise cause inflated/incorrect stats.

 These players' stats will still be able to be accessed if loaded normally (or searched for if
 you're using the default layout). The configuration setting $include_all will overwrite this
 list if you set it to true. It will display the excluded players on the list anyway. If for
 any reason you want to purge the loaded exclude list in a layout script, there are API
 functions to do so. Generally these aren't needed, however.

 To set the Excluded list, simply create a text file in the same location as specified by the
 $exclude_file configuration variable. The default location is in the same directory as the
 script, named "exclude.txt." In this text file almost any obvious format of player names will
 work. You can have all of the player names you want excluded on one line separated by a
 space, a comma, or you can have each name on its own line. The parser will strip out any
 non-alphanumeric, non-underscore characters and limit names to between 2 and 16 characters
 long.

 A working example may be:

   Blah_blah          Another_name, Moogle
   minecraftnewbie | Turtles and_muffins

 All the names should be parsed correctly and added to the list.

 The reasoning behind having a separate text file instead of the excluded list being a direct
 option in the configuration file is to allow easy access to editing the list by admins or
 scripts designed to parse out players of a certain rank.










+---------------------------------------------------------------------------------------------+
¦   VI. Features & Information                                                     [FEATURES] ¦
+---------------------------------------------------------------------------------------------+

 The BeardStat API comes with many functions for retrieving and formatting the data that the
 BeardStat plugin creates. The basic design includes;

 * The ability to look up and display stats for any player that has been recorded.

 * The ability to fully customize exactly which stats are displayed.

 * The ability to display the position that a player has for each stat compared to every other
   player (1st for Most Creepers Killed, 430th for Most Diamonds Mined, as examples).

 * The ability to view a "Highest Stat" list, known as a Top List, with very little coding
   (or none at all if you use the default layout).

 * The ability to exclude certain players from this Top List in the event that they may cause
   incorrect stat values due to being able to spawn items, etc (admins/ops).

 * The ability to look up the total number of players who have stats recorded, or which ones
   have been active in the past X days/weeks/whatever timeframe you want.

 The default layout is also a neutral gray-toned theme that uses gradients to match almost any
 website's layout. It can also be used as a stand-alone web page if needed.

 The default format.ini and listformat.ini files should list most of the stats that people
 care about, so setting up a stats page should be as easy as setting the Database connection
 variables.

 If there's any features you think need to be added, bugs that need squished, or any other
 changes, feel free to contact us on the BeardStat website (listed at the top of this readme).










+---------------------------------------------------------------------------------------------+
¦  VII. Flow Chart                                                                     [FLOW] ¦
+---------------------------------------------------------------------------------------------+

 When using the BeardStat API, there's a general "flow" that the pages should follow. Although
 customization is there, the API works best when applied correctly. Below is a generalization
 of how we expect you to use the API. For information on all of the functions mentioned here,
 refer to the Function List section.

 Before Anything Else
 ====================
   You will need to include the beardstat.php file in the header of your layout script. The
   BeardStat API should attempt to load the config.php file itself from the same directory
   as the layout script. Generally it's best to have the beardstat.php and config.php files
   in the same directory as the layout script. As an example to include the BeardStat API;

   //==========
   require_once("beardstat.php");
   //==========


 Layout
 ======
   Firstly, the layout. You will want to make room for a search box that will be used for
   looking up players, a drop-down list that will show all of the Top Lists and a results
   area where the results will show. Generally the results table should fit into a 600-pixel
   wide area. You may need to adjust it depending on the length of any Stat Names you use in
   format.ini. Smaller font sizes could decrease the needed width as well, if you need it
   smaller.


 Finding Players
 ===============
   When looking up players, you will have to specify your own form element and verification.
   You will need to use the BS_find_player() function. You can pass an "unsafe" string to
   this function (without stripslashes()'ing it, or verifying the name) and it will use it
   without causing errors. This function will return a single name if only one is found, or
   an array if multiple are found. It searches for the string passed to it anywhere within
   the actual player name. If "rawr" is specified, it will match both "rawr123" and
   "i_am_rawrgle."


 If Multiple Players Are Found
 =============================
   If a player search matches multiple names, an array is returned by BS_find_player(). You
   can use is_array() to check if the returned result is an array, then use a foreach loop to
   run through the names. There are no separate API functions to deal with multiple found
   search names (and such functions really shouldn't be necessary).

   As an all-around player searching example;

   //==========
   $player = BS_find_player("an");
   if ($player == -1) {
     echo "Player name must be between 2 and 16 characters long!";
   }
   else if (!$player) {
     echo "No player found!";
   }
   else if (is_array($player)) {
     echo "Multiple names found. Please choose one.<br><br>";
     foreach ($player as $value) {
       echo '<a href="'.$_SERVER['PHP_SELF'].'?player='.$value.'">'.$value.'</a><br>';
     }
   }
   else {
     // Would place code here to load a specific player's Stats page.
     echo "Found the player: ".$player;
   }
   //==========

   -1 is returned if the player name specified isn't within the Minecraft limits of 2 and 16,
   FALSE if no player can be found, an array if multiple players are found, and a string if
   only a single player is found or the search name matches a player's name exactly.


 When Looking At a Stats Page
 ============================
   When a specific player name is found and returned, you can pass it to BS_load_player().
   This function will search the database for the players' stats and then load them into
   memory. Because format.ini and listformat.ini (the Statistics format and Top List format)
   are loaded into memory when the script is called, and contain all of the necessary
   information, you don't need to worry about anything but table structure.

   When it comes to displaying the data, you will need to run through all the Main categories
   and in each Main category run through all of the Stats to be displayed. To do this, you
   need to call BS_next_category(). This will return the name of the next Main category (or
   the first one if it hasn't been called yet, or FALSE if no more categories exist). You can
   pass this Main category to BS_next_stat() which will then return an array of data for the
   next statistic as loaded from format.ini. Each successive call to BS_next_stat() will
   return the next row of data. See the Function List section for more information on these
   functions.

   As an example of how to work this, you can use 2 while loops;

   //==========
   BS_load_player("furdabip");
   echo "<pre>\n";
   while ($cat = BS_next_category()) {
     echo $cat."\n\n";
     while ($stat = BS_next_stat($cat)) {
       print_r($stat);
     }
   }
   echo "</pre>";
   //==========

   This will simply load the stats for the player "furdabip" and then run through each Main
   category, then each stat for that Main category. Of course, this will just dump each
   returned stat's data and not actually format it in any way. That's where you come in to
   format it and make it look the way you want it.

   Sub-categories are supported by the API, and your scripts should support them too. A
   sub-category can be identified by the first value of the returned array being "1." A
   regular stat entry will have the first value as "0." Usually a new sub-category is
   displayed by adding a new header-like row to a table to identify the new section of stats.


 Displaying The Top Lists
 ========================
   The Top Lists are loaded much the same as statistics. You can use BS_next_list() to return
   an array with information about the next Top List. Like BS_next_stat(), it will return
   FALSE if there are no more entries left. The first value returned in the array returned
   will be the ID of that Top List, which we can use to load specific data later.

   You use the same approach code-wise;

   //==========
   echo '<select name="toplist"><option value="">-- Select One --';
   while ($list = BS_next_list()) {
     echo '<option value="'.$list[0].'">'.$list[1];
   }
   echo '</select>';
   //==========

   This will return a very simple drop-down that lists all of the Top List entries. You can
   then code a form to submit the Top List and use BS_load_top_list() to load the results into
   memory. BS_load_top_list() can take a single entry if it is a Top List ID, so we can just
   pass our submitted 'toplist' to it.

   Once the results of a Top List have been loaded, you can use BS_next_top_list() to fetch
   an array of data about the next entry in the results. This will do the same as the other
   "next" function calls and return FALSE if no more exist. The results contain information
   like the player's name, position and the value of the stat.

   As an example of just basic displaying;

   //==========
   BS_load_top_list(0);
   while ($toplist = BS_next_top_list()) {
     echo "[$toplist[0]] $toplist[1] with $toplist[2]<br />";
   }
   //==========

   This loads the first Top List as loaded from listformat.ini and then displays all the
   positions of the players.










+---------------------------------------------------------------------------------------------+
¦ VIII. Function List                                                             [FUNCTIONS] ¦
+---------------------------------------------------------------------------------------------+

 +------------------------------------------------+
 ¦    a. General Functions    [FUNCTIONS_GENERAL] ¦
 +------------------------------------------------+

   gettimeformat( [ int $time [, int $format ]] )
   ==============================================
     The BeardStat API uses this function to format time passed for certain RES_ stat formats.

     Parameters
     ----------
       time          - An integer representing any number of seconds.

       format        - An integer from 0 to 3 representing an internal format for the string
                       returned.

                       Examples:

                       0: 1w 12d 3h 55m 1s
                       1: 1 week, 12 days, 3 hrs, 55 mins, 1 sec
                       2: 1 Week, 12 Days, 3 Hours, 55 Minutes, 1 Second
                       3: Weeks: 1 - Days: 12 - Hours: 3 - Mins: 55 - Secs: 1

     Returned Values
     ---------------
       A string with weeks, days, hours, minutes and/or seconds representing the seconds
       passed found in $time.





   al( [ string $str [, string $extra ]] )
   ====================================
     Returns a string with all non-alphabetical characters removed from $str. Any additional
     characters needed to stay in the string should be specified with $extra. This function is
     case-insensitive.

     Parameters
     ----------
       str           - The string that will be stripped of non-alphabetical characters.

       extra         - A string of extra characters to not strip. Generally specified as
                       "0123456789" for an alphanumerical string.

     Returned Values
     ---------------
       A string with all non-alphabetical characters removed unless they exist within $extra.





   suffix( [ mixed $number ] )
   ===========================
     Returns a proper English ordinal suffix for the $number supplied (1st, 2nd, 3rd, 4th, …
     11th, 12th, 13th, … 45,023rd, 45,024th, etc). This function is included with the
     BeardStat API to be paired up with the BS_stat_position() function (listed under here
     somewhere). It will use the $thousands_separator configuration setting to separate out
     the thousands.

     Parameters
     ----------
       number        - Can be an integer or string representing an integer to have the English
                       ordinal suffix for that number added onto it and returned.

     Returned Values
     ---------------
       Returns a string with the $number grouped into thousands using the $thousands_separator
       configuration setting and the English ordinal suffix for that number added to the end.





   BS_version_string( )
   ====================
     Returns the current version for the BeardStat Web API as a string.

     Returned Values
     ---------------
       A string containing the current version and author(s) of the version and/or revision.

       As an example: "beta 0.1 R1 by Richard Moyer" may be returned.





   BS_load_excluded( [ string $filename ] )
   ========================================
     Loads the Excluded Players List from the file $filename. This function ignores the
     $include_all configuration setting. As long as the file specified can be loaded, this
     function will purge all loaded Excluded Players before attempting to load more.

     Parameters
     ----------
       filename      - A string representing the file location from which to load the Excluded
                       Players List. If your PHP is configured to handle remote file
                       locations, this can be a URL if needed.

                       If a blank string or omitted, the function will attempt to load the
                       Excluded Players List from the file specified in the $exclude_file
                       configuration setting.

     Returned Values
     ---------------
       TRUE is returned if the file can be read from and is attempted to be loaded. It doesn't
       matter if any actual names were added to the list. FALSE is returned if the file
       cannot be read from.





   BS_is_player_excluded( [ string $player ] )
   ===========================================
     Checks the Excluded Players List to see if $player is on it. This function does not
     actually check if the name is a valid one, just if it exists on the Excluded List.

     Parameters
     ----------
       player        - A string representing a player's name from the stats database. If
                       omitted or set as a blank string, the currently loaded player's name
                       will be used to check for.

     Returned Values
     ---------------
       TRUE is returned if the player specified is on the Excluded Players List, or FALSE
       otherwise. If $player is omitted, and no players' stats are currently loaded, FALSE
       will be returned also.





   BS_purge_excluded( )
   ====================
     Removes all loaded Excluded Players from the Excluded List.

     Returned Values
     ---------------
       This function will always return TRUE, regardless of if there's any Excluded Players
       actually loaded.










 +------------------------------------------------+
 ¦    b. Player Functions      [FUNCTIONS_PLAYER] ¦
 +------------------------------------------------+

   BS_total_players( [ int $timestamp ] )
   ======================================
     Returns the number of players recorded in the database.

     Parameters
     ----------
       timestamp     - An integer representing the number of seconds since the Unix Epoch as
                       returned from time(), strtotime() or mktime(). If this is set to a
                       number >= 0, this function will return the number of players who have
                       logged in since that date.

     Returned Values
     ---------------
       The number of players who are in the database, or the number of players who have logged
       in since the date specified in $timestamp.





   BS_find_player( [ string $player [, int $limit [, bool $force = FALSE ]]] )
   ===========================================================================
     Searches the database for players with $player in their name and returns a string or
     array with the results.

     Parameters
     ----------
       player        - The player name to check for. The function will strip all "bad"
                       characters so it is safe to pass an uncleaned string from user input
                       directly to this function.

       limit         - If specified, the function will limit the results returned to that
                       amount. Set to a high number to skip this parameter.

       force         - If set to TRUE, the results returned will always be an array of the
                       names found instead of a string if only one name is found.

     Returned Values
     ---------------
       A string with a single name will be returned if $player matches a player name exactly,
       or only one result is found with that string in it. -1 will be returned if $player is
       < 2 or > 16 characters long (the Minecraft requirements). FALSE will be returned if
       $player is not specified or no players are found. An array of player names as each
       entry will be returned if $player doesn't match a player name exactly, but does match
       more than one name.

       If $force is specified, an array of all player names found (even an exact match) is
       returned. This may be helpful for player lookup scripts.

     Examples
     --------
       //==========
       $player = BS_find_player("an");
       if ($player == -1) {
         echo "Player name must be between 2 and 16 characters long!";
       }
       else if (!$player) {
         echo "No player found!";
       }
       else if (is_array($player)) {
         echo "Multiple names found. Please choose one.<br><br>";
         foreach ($player as $value) {
           echo '<a href="'.$_SERVER['PHP_SELF'].'?player='.$value.'">'.$value.'</a><br>';
         }
       }
       else {
         // Would place code here to load a specific player's Stats page.
         echo "Found the player: ".$player;
       }
       //==========





   BS_load_player( [ string $player ] )
   ====================================
     Retrieves and stores the specified $player's statistics into memory to be processed by
     other functions.

     Parameters
     ----------
       player        - The player name to load from the database. The function will clean any
                       potentially "unsafe" names specified by user input, so it is okay to
                       pass an uncleaned string to this function.

     Returned Values
     ---------------
       Returns TRUE if the player was found and properly loaded into memory or FALSE if
       $player was omitted or no exact player name match was found.





   BS_loaded_player( )
   ===================
     Checks if a player's statistics are currently loaded into memory and returns their name
     if so.

     Returned Values
     ---------------
       Returns a string containing the name of the currently loaded player's statistics, or
       FALSE if no player is loaded.





   BS_purge_player( )
   ==================
     Purges the currently loaded player's statistics from memory. This function isn't usually
     necessary to call before loading a second player as calling BS_load_player() will
     automatically purge the old data before loading the new player's statistics.

     Returned Values
     ---------------
       This function will always return TRUE, regardless of if a player's statistics are
       actually loaded or not.










 +------------------------------------------------+
 ¦    c. Category Functions       [FUNCTIONS_CAT] ¦
 +------------------------------------------------+

   BS_next_category( )
   ===================
     Returns the current Main category the array pointer is on then advances the pointer to
     the next entry. FALSE is returned if no more categories exist. Main categories are used
     to run through each statistic specified in format.ini. If you've skipped over it, you
     should head back up to the Flow Chart section for information on how to properly use Main
     categories as well as sub-categories and statistics.

     Returned Values
     ---------------
       Returns a string representing the current Main category as specified in format.ini or
       FALSE if no more categories exist.





   BS_current_category( )
   ======================
     Returns the current Main category the array pointer is on, or the last one loaded if no
     more categories exist.

     Returned Values
     ---------------
       Returns a string representing the current Main category as specified in format.ini or
       the last Main category loaded from format.ini if the pointer is past the last entry.





   BS_reset_category( )
   ====================
     Resets the array pointer for Main categories back to the first entry and returns that
     entry.

     Returned Values
     ---------------
       Returns a string representing the first Main category as specified in format.ini.










 +------------------------------------------------+
 ¦    d. Statistic Functions     [FUNCTIONS_STAT] ¦
 +------------------------------------------------+

   BS_next_stat( [ string $category ] )
   ==============
     Returns an array of information about the current Statistic the array pointer is on then
     advances the pointer to the next entry.

     Parameters
     ----------
       category      - The Main category as returned by BS_next_category() or
                       BS_current_category(). An exact string could be used as long as it
                       matched a Main category as specified in format.ini, but generally it's
                       best to rely on the category functions.

                       If set to an empty string or omitted, the category will attempt to be
                       loaded from BS_current_category(). This may cause unexpected results if
                       you are using BS_next_category() in a while loop as BS_next_category()
                       will increase the array pointer after it returns the next Main
                       category, causing the next call to BS_current_category() to be the
                       entry AFTER the value returned from BS_next_category(). Confused? Read
                       the Flow Chart section for details and examples of how to handle Main
                       categories properly.

     Returned Values
     ---------------
       An array containing information about the Statistic entry is returned if $category is
       a valid Main category and a player's statistics are currently loaded into memory. FALSE
       is returned if no other Statistics exist for the $category or no player's statistics
       are loaded into memory.

       Refer to the BS_current_stat() function entry below this for which array values contain
       which data.





   BS_current_stat( [ string $category ] )
   =======================================
     Returns an array of information about the current Statistic the array pointer is on.

     Parameters
     ----------
       category      - The Main category as returned by BS_next_category() or
                       BS_current_category().

                       If set to an empty string or omitted, the category will attempt to be
                       loaded from BS_current_category(). This may cause unexpected results.

     Returned Values
     ---------------
       An array containing information about the Statistic entry is returned if $category is
       a valid Main category and a player's statistics are currently loaded into memory. FALSE
       is returned if no player's statistics are loaded into memory or the $category is
       invalid.

       The array returned contains the following data;

       Array (
         [0] - The Data Type of the data returned. If the data is a standard Statistic entry,
               this will be 0. If it is a sub-category, this will be 1. You will most likely
               check this value for coding sub-categories into your layout.

         [1] - If the entry is a standard Statistic entry, this will be the Stat Description
               as specified in format.ini. If the entry is a sub-category, this will be the
               sub-category's text.

         [2] - The Stat value that has been processed for display (grouped into thousands or
               converted to a date if necessary).

         [3] - The raw, unformatted Stat value.

         [4] - The database category that was used to get the ultimate Stat value, as
               specified in format.ini.

         [5] - The raw "+"-separated list of database stat entries that was combined together
               to get the ultimate Stat value.

         [6] - An array of the stats used to get the ultimate Stat value. Basically an
               explode()'d version of [5].
       )





   BS_reset_stat( [ string $category ] )
   =====================================
     Resets the array pointer for the $category specified and returns an array of information
     about the first entry.

     Parameters
     ----------
       category      - The Main category as returned by BS_next_category() or
                       BS_current_category() to be reset.

                       If set to an empty string or omitted, the category will attempt to be
                       loaded from BS_current_category(). This may cause unexpected results.

     Returned Values
     ---------------
       An array containing information about the first Statistic entry is returned if
       $category is a valid Main category and a player's statistics are currently loaded into
       memory. FALSE is returned if no player's statistics are loaded into memory or $category
       isn't a valid Main category.

       Refer to the BS_current_stat() function entry above this for which array values contain
       which data.





   BS_reset_all_stats( )
   =====================
     Resets the array pointer for each Main category and then resets the array pointer for the
     Main categories themselves. In other words, both BS_*_category() and BS_*_stat()
     functions will start back from the first entry.

     Returned Values
     ---------------
       Returns TRUE upon success, or FALSE if no player's statistics are currently loaded into
       memory.





   BS_stat_position([ string $category [, array $stats [, string $player [, string $aord ]]]])
   ===========================================================================================
     Returns the position (or rank if you will) of a specific player compared to the rest of
     the players in the database. This function queries the database everytime it is called,
     so may cause lag if it is utilized for every Statistic as defined in format.ini. If
     $player is the same as the player whose statistics are currently loaded, the stats to be
     compared will be loaded from there.

     Parameters
     ----------
       category      - The statistic category as defined in the database. "stats,"
                       "itempickup," etc.

       stats         - An array of the stats to be added together to compare for the position.
                       If this is a string, it will be converted to an array with the
                       assumption that it is a "+"-separated list of stats, as the same format
                       that appears in format.ini and listformat.ini. As an example;
                       array("dirt","grass") for dirt + grass amount, or array("sand") for
                       just sand.

       player        - The player whose stats you are checking for their position. If player
                       is set to an empty string or omitted, the function will attempt to
                       load the player's name from the currently loaded player's statistics.

       aord          - Should be set to "asc" for an ascending check (rarely used) or "desc"
                       for a descending check. If it is set to anything else, it will default
                       to descending.

     Returned Values
     ---------------
       Upon success this function will return a number between 1 (first) and the total number
       of players in the database. This result will go well when combined with suffix().

       If the $category or the stats in $stats are not found for whatever reason, or they are
       not specified, FALSE is returned. Since the function only checks existing recorded
       statistics, there's a rare-ish chance that statistics may not have been recorded yet
       but are still valid. In this case, FALSE will be falsely returned.

       If the $player specified is on the Excluded Players List, -1 is returned. -1 is also
       returned in the event that the total of the stats in $stats is equal to 0, unless $aord
       is set to "asc," in which case the position will be returned as expected.

       For clarification, say you specify "blockcreate" as the category and "dirt+grass" as
       the stats. This would normally return the player's position for total grass and dirt
       placed when combined together, but if the player hasn't placed ANY dirt or grass, their
       total would be 0, and -1 would be returned instead of a position. The function behaves
       like this as there's no point listing an "Nth position" for a player that hasn't even
       recorded those stats yet.










 +------------------------------------------------+
 ¦    e. Top Lists Functions      [FUNCTIONS_TOP] ¦
 +------------------------------------------------+

   BS_next_list( )
   ===============
     Returns an array of information about the current Top List the array pointer is on then
     advances the pointer to the next entry.

     Returned Values
     ---------------
       An array of information is returned if there are still entries in the Top List as
       loaded from listformat.ini, or FALSE if there are no more Top Lists.

       Refer to the BS_current_list() function entry below this for which array values contain
       which data.





   BS_current_list( [ int $specific ] )
   ====================================
     Returns an array of information about the current Top List the array pointer is on. If
     $specific is specified and matches a Top List ID as assigned when loaded from
     listformat.ini, then this function will return the values from that Top List instead of
     the Top List the array pointer is currently on.

     Parameters
     ----------
       specific      - Set to the Top List ID, as assigned when loaded from listformat.ini in
                       order to return the data from that specific Top List.

                       Since there's no ID specified in the listformat.ini format, the entries
                       are assigned one as they are loaded going top to bottom from the file.
                       The first entry is assigned the ID of 0, the next 1, then 2, and so on.
                       BS_search_list() may be a better function to use in the event that you
                       don't know the exact order of listformat.ini.

     Returned Values
     ---------------
       An array of information is returned for the current entry, or the last entry if there
       are no more entries left.

       The array returned contains the following data;

       Array (
         [0] - The Top List ID as assigned when loaded from listformat.ini. Will always be an
               integer.

         [1] - The specified Top List Title.

         [2] - The specified Top List Description.

         [3] - Will be "asc" if the Top List is set to be calculated in ascending order,
               otherwise it will be "desc."

         [4] - The specified database category that will be used to retrieve the Top List.

         [5] - A raw string of "+"-separated database stats that will be used to retrieve the
               Top List.

         [6] - An array of all of the database stats specified that will be used to retrieve
               the Top List. Basically an explode()'d version of [5].
       )





   BS_reset_list( )
   ================
     Resets the array pointer for the Top Lists and returns an array of information about the
     first entry.

     Returned Values
     ---------------
       An array of information about the first Top List.

       Refer to the BS_current_list() function entry above this for which array values contain
       which data.





   BS_search_list( [ string $category [, array $stats ]] )
   =======================================================
     Searches the Top Lists for an entry that matches the database $category specified and has
     at least one of the stat entries specified in the $stats array.

     Parameters
     ----------
       category      - The database category to search the Top Lists for.

       stats         - An array containing the stats you want to search the Top Lists for. If
                       this is not an array, it will be converted to one with the assumption
                       that it is a "+"-separated list of stats.

     Returned Values
     ---------------
       An array of information about the first Top List found with the search criteria is
       returned. If no Top List is found, FALSE is returned. FALSE is also returned if you 
       omit $category or $stats.

       Refer to the BS_current_list() function entry above this for which array values contain
       which data.





   BS_load_top_list([mixed $catorid[,int $top[,array $stats[,string $format[,string $aord]]]]])
   ============================================================================================
     Loads a Top List from the database into memory to be displayed by other BS_*_top_list()
     functions.

     Parameters
     ----------
       catorid       - The database category to be used to load the Top List, or the ID of the
                       Top List as assigned when loaded from listformat.ini. If you specify
                       the Top List ID, you don't need to specify any other parameters as they
                       will be loaded from that Top List.

       top           - The number of results to return for the loaded Top List. If omitted or
                       set to 0, this will default to the $default_top_list configuration
                       setting. So... set to 0 to skip this parameter.

       stats         - An array of the stats that will be added together to get the Top List.
                       If this is not an array, it will be converted to one with the
                       assumption that it is a "+"-separated list of stats.

       format        - This is the format string the result will be pumped into. "%" will be
                       replaced by the total of the stats added together. As an example of
                       a "stats" category with "move" as the stat to compare and "% meters" as
                       the format, you'd get something like;

                       "Distance Traveled on Foot: 4,671,994 meters"

       aord          - Should be set to "asc" for an ascending check (rarely used) or "desc"
                       for a descending check. If it is set to anything else, it will default
                       to descending.

     Returned Values
     ---------------
       Returns TRUE if a Top List was successfully loaded, even if the Top List contained no
       players in it. You can call BS_loaded_top_list() or check
       $BSLoaded['current_top_results'] to see if 0 players were found.

       FALSE is returned if $catorid or $stats is not specified.





   BS_loaded_top_list( )
   =====================
     Returns information about the currently loaded Top List.

     Returned Values
     ---------------
       An array of information about the currently loaded Top List, or FALSE if a Top List
       isn't currently loaded.

       The array returned contains the following data;

       Array (
         [0] - The number of actual results found.

         [1] - The number of EXPECTED results. This is the $top value passed to
               BS_load_top_list() or the $default_top_list configuration setting if $top was
               omitted or skipped.

         [2] - The Top List ID if an ID was used to load the Top List. A blank string
               otherwise.

         [3] - The database category used to load the Top List.

         [4] - A raw "+"-separated list of the stats used to load the Top List.

         [5] - The format specified when the Top List was loaded.

         [6] - "asc" or "desc" for ascending or descending order when the Top List was loaded.
       )





   BS_purge_top_list( )
   ====================
     Purges the currently loaded Top List from memory.

     Returned Values
     ---------------
       This function always returns TRUE, even if no Top List is actually loaded.





   BS_next_top_list( )
   ===================
     Returns an array of information about the current Top List position, then advances the
     array pointer to the next entry.

     Returned Values
     ---------------
       Returns an array of information about the current Top List, or FALSE if no Top List is
       currently loaded or no more positions exist.

       Refer to the BS_current_top_list() function entry below this for which array values
       contain which data.





   BS_current_top_list( [ int $specific ] )
   ========================================
     Returns an array of information about the current Top List position.

     Parameters
     ----------
       specific      - If specified and is a valid position, the information for that position
                       in the Top List is returned instead of the one the array pointer is
                       currently on. This does not affect the array pointer's position.

                       As an example, BS_current_top_list(1) would return information about
                       the player results in first place. BS_current_top_list(12) would be the
                       twelvth place.

     Returned Values
     ---------------
       Returns an array of information about the current Top List, or FALSE if no Top List is
       currently loaded.

       The array returned contains the following data;

       Array (
         [0] - Current position. 1 for first place, 2 for second, etc.

         [1] - Name of the player for this position.

         [2] - The formatted and processed result for this position. "4,671,994 meters" as an
               example.

         [3] - The unformatted result. "4671994" as an example.
       )





   BS_reset_top_list( )
   ====================
     Resets the currently loaded Top List's array pointer to the first result and returns an
     array of information about it.

     Returned Values
     ---------------
       Returns an array of information about the first Top List result, or FALSE if no Top
       List is currently loaded.

       Refer to the BS_current_top_list() function entry above this for which array values
       contain which data.










+---------------------------------------------------------------------------------------------+
¦   IX. Variable References                                                       [VARIABLES] ¦
+---------------------------------------------------------------------------------------------+

 The data that the BeardStat Web API uses is stored in variables that you can access at any
 time if you need to, however it's best to rely on the functions provided. The variables used
 may change in future versions. Either way, here is a list of all the variables the
 BeardStat Web API creates and what they are used for;


 Version Information
 ===================
   BS_DEVVERSION: The development version. Will be "alpha," "beta," or "release."

   BS_VERSION: The current main version number. If you code all new features or functionality
               into the BeardStat Web API, you should increase this and reset the revision to
               1. Also don't forget to set the BS_AUTHOR and BS_REVISIONAUTHOR.
 
   BS_REVISION: The revision number for the current version. Will start back at 1 for each new
                version. Don't forget to increase this for each minor release you may code for
                the BeardStat Web API.

   BS_AUTHOR: The author of the current version. If you code a new version of the BeardStat
              Web API, don't forget to set this to your name so people know.

   BS_REVISIONAUTHOR: The author of the current revision. If you change some minor thing in
                      the BeardStat Web API, don't forget to update this with your name.


 Constants
 =========
   BS_CFG_DBNAME: The database name as defined in the configuration file as $database_name.
                  It's used for MySQL queries.

   BS_CFG_TABLENAME: The table name where the stats are stored. This defaults to "stats."

   BS_CFG_TFORMAT: The $time_format configuration setting.

   BS_CFG_DFORMAT: The $date_format configuration setting.

   BS_CFG_ONLINETEXT: The $time_online configuration setting.

   BS_CFG_TSEPARATOR: The $thousands_separator configuration setting.

   BS_CFG_ALPHABETIZE: The $alphabetize_list configuration setting.

   BS_CFG_DEFAULTTOP: The $default_top_list configuration setting.

   BS_CFG_EXCLUDEFILE: The $exclude_file configuration setting.

   BS_CFG_INCLUDEALL: The $include_all configuration setting.


 Stored Data Variables
 =====================
   $BSFormat: The master array of the Statistics loaded from format.ini. It will contain an
              array with each entry being another array with the key of a Main category. Each
              of those arrays will then contain an array with the formatting data. Ultimately
              it will look like so;

              Array (
                [Main Category Name] - Array (
                  [Raw category~stats+used+for+the+statistic] - Array (
                    [0] - Description
                    [1] - Format
                  )
                  …
                )
                …
              )

   $BSListFormat: The master array of the Top Lists loaded from listformat.ini. It will
                  contain an array with each key being the Top List ID, and the value being
                  another array of information. Ultimately it will look like so;

                  Array (
                    [Top List ID] - Array (
                      [0] - Raw category~stats+used+for+the+list string.
                      [1] - Top List Title.
                      [2] - Top List Description.
                      [3] - Format.
                      [4] - "asc" or "desc."
                    )
                    …
                  )

   $BSExcluded: This will contain an array with each entry being a player name to be excluded.

   $BSExcluded_SQL: This is just an insert needed for the SQL calls. It's filled when an
                    Excluded Players List is loaded, and emptied when purged. It's probably a
                    good idea to not mess with this variable, as it's pumped directly into SQL
                    queries. You will probably EXPLODE THE ENTIRE WORLD if you mess it up!

   $BSLoaded: This is an array containing all the "loaded" data. Loaded player stats and
              loaded Top Lists are stored here. It is an array with the following entries;

              Array (
                ['current_player'] - The currently loaded player's name, or a blank string.

                ['current_player_stats'] - An array of categories and the stats in each of
                                           those categories in another array. Will be an empty
                                           array if no player's stats are loaded. The loaded
                                           array will be as such;

                                           Array (
                                             [category] - Array (
                                               [stat] - Raw stat value.
                                               …
                                             )
                                             …
                                           )

                ['current_top_results'] - Will be the number of positions in a loaded Top
                                          List, or -1 if no Top List is loaded.

                ['current_top_eresults'] - Will be the number of EXPECTED positions in a
                                           loaded Top List, or -1 if no Top List is loaded.

                ['current_top_id'] - Will be the Top List ID if one was used in
                                     BS_load_top_list() to load the Top List data
                                     automatically, otherwise a blank string.

                ['current_top_category'] - Will be the category used to load the Top List, or
                                           a blank string.

                ['current_top_stat'] - Will be the raw "+"-separated stats string used to load
                                       the Top List, or a blank string.

                ['current_top_format'] - Will be the format used when loading the Top List, or
                                         "%" otherwise.

                ['current_top_aord'] - Will either be "asc" or "desc" depending on which was
                                       used to load the Top List.

                ['current_top_list'] - Will be an array of arrays containing all the Top List
                                       entries of a loaded Top List, or an empty array. The
                                       array will contain this data;

                                       Array (
                                         [] - Array (
                                           [0] - List Position.
                                           [1] - Player Name
                                           [2] - Processed and formatted stat value.
                                           [3] - Raw stat value.
                                         )
                                       )
              )










+---------------------------------------------------------------------------------------------+
¦    X. Database Entries Reference                                                 [DATABASE] ¦
+---------------------------------------------------------------------------------------------+

 The stats listed here are in groups of stat categories and under each are all the stats used
 by that category. A category or even a stat won't exist until a player has recorded it. This
 won't affect the layout format specified in format.ini if a statistic doesn't exist for that
 player yet, as a 0 is returned in that case.

 To use these properly in format.ini and listformat.ini, you should use:

 Category~Each+Stat+To+Be+Added+Together=[Whatever other data needed for that specific format]

 If you only want one stat to represent the entry, you don't need to specify a "+" anywhere,
 obviously. The "+" feature exists for such things as "Total Music Discs obtained" or "Dirt
 Dug" as both grass and dirt blocks count differently, so you can use grass+dirt. The best way
 to see how it should be done is to just see how the default format.ini and listformat.ini do
 it.

 There are common lists of stats that multiple categories will use at the bottom of this
 section. As an example, damagetaken and deaths both have all the [Entities] and
 [Damage Cause] entries as a possible entry for their category. Things that wouldn't make
 sense like deaths -> snowball will never be recorded unless for some reason Snowballs are set
 to do damage to players. See the Stat Glossary subsection for these lists.





 stats               - General Statistics.
 =============================================================================================
   totalblockcreate  - Total number of blocks placed.
   totalblockdestroy - Total number of blocks mined/destroyed.

   playedfor         - Number of seconds played on the server.
   login             - Number of times logged into the server.
   firstlogin        - First time logged into the server (as a timestamp).
   lastlogin         - The last time logged into the server (as a timestamp).
   lastlogout        - The last time logged out of the server (as a timestamp).

   chatletters       - Number of characters said in chat messages total.
   chat              - Number of chat messages sent.
   kicks             - Number of times kicked from the server.

   damagehealed      - Amount of health recovered.
   healeating        - Health regained from eating.
   healmagic         - Health regained from potions/spell effects.
   healmagicregen    - Health regenerated over time from potions/spell effects.
   healregen         - Health regenerated naturally over time (as from Peaceful difficulty).
   healsatiated      - Health regenerated from having a full Hunger bar.

   tamewolf          - Number of wolves tamed.
   fishcaught        - Number of Fish caught.

   armswing          - Number of times arm was swung (as from clicking on something).
   move              - Number of blocks walked.
   portal            - Number of times used a portal (either Nether/End).
   teleport          - Number of times teleported.
   openchest         - Number of times opened a chest.

   fillwater         - Number of times filled a bucket with Water.
   filllava          - Number of times filled a bucket with Lava.
   emptywater        - Number of times emptied a bucket of Water.
   emptylava         - Number of times emptied a bucket of Lava.


 blockcreate         - Blocks placed down.
 =============================================================================================
   [Blocks]          - See the Stat Glossary for this list of Block types.


 blockdestroy        - Blocks mined/destroyed/broken.
 =============================================================================================
   [Blocks]          - See the Stat Glossary for this list of Block types.


 itempickup          - Blocks and/or items picked up off of the ground.
 =============================================================================================
   [Blocks]          - See the Stat Glossary for the list of Block types.
   [Items]           - See the Stat Glossary for the list of Item types.


 itemdrop            - Blocks and/or items dropped from the inventory.
 =============================================================================================
   [Blocks]          - See the Stat Glossary for the list of Block types.
   [Items]           - See the Stat Glossary for the list of Item types.


 itemuse             - Various items placed/used.
 =============================================================================================
   tnt               - Number of TNT detonated.
   enderpearl        - Number of Ender Pearls used.
   flintandsteel     - Number of fires started with Flint & Steel.
   sign              - Number of Signs placed.
   bucket            - Number of Buckets used (either filled or emptied).
   cakeblock         - Number of slices of Cake eaten.


 interact            - Various interactions with mobs.
 =============================================================================================
   milkcow           - Number of cows milked.
   milkmushroomcow   - Number of Mooshrooms milked for Mushroom Stew.


 dye                 - Dying sheep.
 =============================================================================================
   total             - Number of Sheep dyed.


 sheared             - Shearing Sheep/Mooshrooms.
 =============================================================================================
   sheep             - Number of Sheep sheared.
   mushroomcow       - Number of Mooshroom sheared of their Mushrooms.


 crafting            - Blocks and/or items created from Crafting.
 =============================================================================================
   [Blocks]          - See the Stat Glossary for the list of Block types.
   [Items]           - See the Stat Glossary for the list of Item types.


 bow                 - Bow-related statistics.
 =============================================================================================
   shots             - Number of shots fired.
   fireshots         - Number of flame arrows fired.
   infiniteshots     - Number of arrows fired from an Infinite bow.


 exp                 - Experience-related statistics.
 =============================================================================================
   lifetimexp        - Total XP ever earned.
   currentexp        - Amount of XP at the current moment.
   currentlvl        - Current XP level.


 enchant             - Enchanting-related statistics.
 =============================================================================================
   total             - Total Enchantments applied to items.
   totallvlspent     - Total XP levels spent on Enchantments.


 potions             - Potions used statistics.
 =============================================================================================
   splashhit         - Number of times hit by a Splash Potion.

   [Potions]         - See the Stat Glossary for the list of Potion types.


 vehicle             - Vehicle-related statistics.
 =============================================================================================
   boat              - Total distance traveled while in a boat.
   minecart          - Total distance traveled while in a minecart.


 damagetaken         - Amount of damage taken from various things.
 =============================================================================================
   total             - The total amount of damage taken from all sources.

   [Entities]        - See the Stat Glossary for the list of Entities.
   [Damage Cause]    - See the Stat Glossary for the list of Damage Causes.


 damagedealt         - Amount of damage dealt to various things.
 =============================================================================================
   total             - The total amount of damage done to all things.

   [Entities]        - See the Stat Glossary for the list of Entities.
   [Damage Cause]    - See the Stat Glossary for the list of Damage Causes.


 kills               - Amount of things killed.
 =============================================================================================
   total             - The total number of things killed.

   [Entities]        - See the Stat Glossary for the list of Entities.
   [Damage Cause]    - See the Stat Glossary for the list of Damage Causes.


 deaths              - Amount of times died to various things.
 =============================================================================================
   total             - The total number of times died.

   [Entities]        - See the Stat Glossary for the list of Entities.
   [Damage Cause]    - See the Stat Glossary for the list of Damage Causes.










 +------------------------------------------------+
 ¦    a. Stat Glossary                 [GLOSSARY] ¦
 +------------------------------------------------+

 [Blocks]            - Block names for stats.
 =============================================================================================
   air               - This will more than likely never be used.
   stone
   grass
   dirt
   cobblestone
   wood              - Wood Planks.
   sapling
   bedrock
   water
   stationarywater
   lava
   stationarylava
   sand
   gravel
   goldore
   ironore
   coalore
   log               - Tree Log.
   leaves
   sponge
   glass
   lapisore
   lapisblock
   dispenser
   sandstone
   noteblock
   bedblock          - Can be used with blockcreate/blockdestroy for Beds placed/destroyed.
   poweredrail
   detectorrail
   pistonstickybase
   web
   longgrass         - Tall Grass.
   deadbush          - The ones found in Deserts.
   pistonbase
   pistonextension   - Will count if this bit is broken for both Normal/Sticky Pistons.
   wool              - Counts for ALL Wool colors.
   pistonmovingpiece - Shouldn't normally be tracked.
   yellowflower
   redrose
   brownmushroom
   redmushroom
   goldblock
   ironblock
   doublestep        - Slabs.
   step              - Slabs.
   brick
   tnt
   bookshelf
   mossycobblestone
   obsidian
   torch
   fire
   mobspawner
   woodstairs
   chest
   redstonewire
   diamondore
   diamondblock
   workbench         - Crafting Table.
   crops             - Can be used with blockcreate for "Wheat Seeds Planted."
   soil              - Can be added with grass+dirt for a more accurate "Dirt Dug" stat.
   furnace
   burningfurnace    - Can't normally place, but should be included with "Furnaces Destroyed."
   signpost          - blockdestroy~signpost+wallsign for "Signs Destroyed."
   woodendoor
   ladder
   rails
   cobblestonestairs
   wallsign
   lever
   stoneplate        - Stone Pressure Plate.
   irondoorblock     - Iron Door.
   woodplate         - Wooden Pressure Plate.
   redstoneore       - blockdestroy~redstoneore+glowingredstoneore for "Redstone Mined."
   glowingredstoneore
   redstonetorchoff  - redstonetorchoff+redstonetorchon for Redstone Torches.
   redstonetorchon
   stonebutton
   snow              - Snow Caps (not blocks).
   ice
   snowblock
   cactus
   clay
   sugarcaneblock
   jukebox
   fence
   pumpkin
   netherrack
   soulsand
   glowstone
   portal            - A Nether Portal block as appears when you light a Nether Portal.
   jackolantern
   cakeblock         - The block of Cake when it's placed on the ground.
   diodeblockoff     - Redstone Repeater (unpowered).
   diodeblockon      - Redstone Repeater (powered).
   lockedchest       - 2011 April Fools "Locked Chest" block.
   trapdoor
   monstereggs       - "Hidden Silverfish" block.
   smoothbrick       - Stone Bricks.
   hugemushroom1     - Huge Brown Mushroom pieces.
   hugemushroom2     - Huge Red Mushroom pieces.
   ironfence         - Iron Bars.
   thinglass         - Glass Panes.
   melonblock
   pumpkinstem
   melonstem
   vine
   fencegate
   brickstairs
   smoothstairs      - Stone Brick Stairs.
   mycel             - Mycelium.
   waterlily         - Lily Pad.
   netherbrick
   netherfence
   netherbrickstairs
   netherwarts
   enchantmenttable
   brewingstand
   cauldron
   enderportal
   enderportalframe
   enderstone
   dragonegg
   redstonelampoff
   redstonelampon    - redstonelampon+redstonelampoff for Redstone Lamps.


 [Items]             - Item names for stats.
 =============================================================================================
   ironspade         - Iron Shovel.
   ironpickaxe
   ironaxe
   flintandsteel
   apple
   bow
   arrow
   coal
   diamond
   ironingot
   goldingot
   ironsword
   woodsword
   woodspade         - Wooden Shovel.
   woodpickaxe
   woodaxe
   stonesword
   stonespade        - Stone Shovel.
   stonepickaxe
   stoneaxe
   diamondsword
   diamondspade      - Diamond Shovel.
   diamondpickaxe
   diamondaxe
   stick
   bowl
   mushroomsoup
   goldsword
   goldspade         - Gold Shovel.
   goldpickaxe
   goldaxe
   string
   feather
   sulphur           - Gunpowder.
   woodhoe
   stonehoe
   ironhoe
   diamondhoe
   goldhoe
   seeds             - Wheat Seeds.
   wheat
   bread
   leatherhelmet
   leatherchestplate
   leatherleggings
   leatherboots
   chainmailhelmet
   chainmailchestplate
   chainmailleggings
   chainmailboots
   ironhelmet
   ironchestplate
   ironleggings
   ironboots
   diamondhelmet
   diamondchestplate
   diamondleggings
   diamondboots
   goldhelmet
   goldchestplate
   goldleggings
   goldboots
   flint
   pork              - Raw Porkchop.
   grilledpork       - Cooked Porkchop.
   painting
   goldenapple
   sign
   wooddoor
   bucket
   waterbucket
   lavabucket
   minecart
   saddle
   irondoor
   redstone          - Redstone Dust.
   snowball
   boat
   leather
   milkbucket
   claybrick         - A single cooked Clay Brick.
   clayball          - An uncooked Clay Ball.
   sugarcane
   paper
   book
   slimeball
   storageminecart
   poweredminecart
   egg
   compass
   fishingrod
   watch             - Clock.
   glowstonedust
   rawfish
   cookedfish
   inksack           - Actually consists of ALL of the Dyes.
   bone
   sugar
   cake
   bed
   diode             - Redstone Repeater.
   cookie
   map
   shears
   melon             - Melon Slice.
   pumpkinseeds
   melonseeds
   rawbeef
   cookedbeef        - Steak.
   rawchicken
   cookedchicken
   rottenflesh
   enderpearl
   blazerod
   ghasttear
   goldnugget
   netherstalk       - Nether Wart.
   potion            - Includes every type of Potion.
   glassbottle
   spidereye
   fermentedspidereye
   blazepowder
   magmacream
   brewingstanditem  - The Item version of the Brewing Stand.
   cauldronitem      - The Item version of the Cauldron.
   eyeofender
   speckledmelon     - Glistering Melon.
   monsteregg        - Spawn Eggs of all types.
   expbottle         - Bottle o' Enchanting.
   fireball          - Fire Charge item.
   goldrecord        - Music Disc "13"
   greenrecord       - Music Disc "cat"
   record3           - Music Disc "blocks"
   record4           - Music Disc "chirp"
   record5           - Music Disc "far"
   record6           - Music Disc "mall"
   record7           - Music Disc "mellohi"
   record8           - Music Disc "stal"
   record9           - Music Disc "strad"
   record10          - Music Disc "ward"
   record11          - Music Disc "11"


 [Entities]          - Hostile/Friendly mobs as well as other things.
 =============================================================================================
   arrow             - A fired arrow from a Dispenser.
   snowball
   egg
   thrownexpbottle   - Not tracked currently, but is a thrown Bottle o' Enchanting
   fireball          - Ghast fireball.
   smallfireball     - Blaze fireball/Fire Charge.

   enderpearl
   primedtnt         - Isn't the "TNT Damage" stat. blockexplosion from [Damage Cause] is.
   minecart
   boat

   creeper
   skeleton
   spider
   giant             - The unused Giant mob.
   zombie
   slime
   ghast
   pigzombie         - Zombie Pigman.
   enderman
   cavespider
   silverfish
   blaze
   magmacube
   enderdragon
   pig
   sheep
   cow
   chicken
   squid
   wolf
   ocelot
   mushroomcow       - Mooshroom.
   snowman           - Snow Golem.
   villager
   irongolem

   endercrystal
   splashpotion

   player            - Any damage to/from another Player's stored as this stat.


 [Potions]           - The Potion names when they are used.
 =============================================================================================
   splashwater
   splashregen
   splashspeed       - Potion of Swiftness.
   splashfireresistance
   splashpoison
   splashinstantheal - Potion of Healing.
   splashweakness
   splashstrength
   splashslowness
   splashinstantdamage - Potion of Harming.


 [Damage Cause]      - Various causes of damage/deaths.
 =============================================================================================
   contact           - Damage from touching something (as from Cactus).
   entityattack      - Damage from a Mob or other Entities.
   projectile        - Damage from arrows, etc.
   suffocation       - Damage from being suffocated in a wall/gravel/sand.
   fall              - Damage from falling.
   fire              - Damage from standing in a fire.
   firetick          - Damage from being on fire.
   melting           - Related to snow golems, should never come up
   lava              - Damage from being in lava.
   drowning          - Damage from drowning.
   blockexplosion    - Damage from TNT exploding.
   entityexplosion   - Damage from Creeper explosions specifically.
   void              - Damage from falling into the Void.
   lightning         - Damage from a lightning bolt.
   suicide           - Damage caused by using the /kill command.
   starvation        - Damage from Hunger bar being empty.
   poison            - Damage from being Poisoned (as from a Cave Spider).
   magic             - Damage from Potions of Harming.
   custom            - Damage from other Plugins.










+---------------------------------------------------------------------------------------------+
¦   XI. License & Copyright Information                                             [LICENSE] ¦
+---------------------------------------------------------------------------------------------+

 Now I ain't no fancy big city lawyer. I'm just a simple country Hyper-Chicken here. We don't
 be needing no complicated License, now will we be? You and I can come to simple terms about
 this whole mess. Just don't be claiming code as your own when you did not write it, don't
 distribute this here BeardStat Web API without this readme document with it and don't remove
 any copyrights you see. Simple, don'tcha think?


 Now the actual License and Copyright instead of a Futurama-themed one.
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


 A copy of this License and Copyright is provided inside the source code for the BeardStat Web
 API. If this License and Copyright does not match, word for word, the same License and
 Copyright contained in this readme, it has been tampered with and you should not use that
 distribution of the BeardStat Web API.










[Written in and optimized for Notepad on Windows 7 - 96 cols wide, Courier New Regular 10pt]
[89,820 Bytes (87.71 KB) on 2,236 Lines]
[End of File]
