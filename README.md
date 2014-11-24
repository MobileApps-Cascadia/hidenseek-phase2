hidenseek-phase2
================

Second round for HideNSeek app.

This app was started in the Spring of 2014.  The original idea was Aaron Campbell's and with the help of Barbara Greenlee they created the base foundation of the game.  

Purpose:
The Hide ‘n’ Seek app is a tool for bold, interactive gameplay that crosses the boundaries of traditional phone-based entertainment. 

This app provides a GPS-assisted version of the classic multi-player game hide-and-seek, enabling larger and more exciting matches.

A sandbox gameplay mode is also provided, wherein all users are given a map which pinpoints the location of every user in the match. This may be used to keep track of people at outdoor events or in many other ways, subject only to your creativity. 


Audience:
School students: Great game for junior high and other students. The supervisor mode would enable a designated adult to monitor the game and the locations of the students.

Recreationists: Campers and other outdoor recreationists may find hide-n-seek to be a fun activity. Additionally, groups of hikers and such could use the sandbox gameplay to keep track of all members

Tourists and shoppers: When touring and shopping in a group, the sandbox gameplay would allow users to keep track of the rest of the group. Standard gameplay would provide an interesting twist, combining the fun of browsing shops with the fun of traditional hide and seek.

Use Cases
  Use Case 1
    Start game as seeker or admin
      Description: a splash screen loads with a timer that was designated by the seeker or admin.  The maps are loaded in the       background.  The timer ends.
      Post-Condition: The maps are loaded and the game has begun.
      Exception: 
      Must be a minimum number pf players. 
      Location services not turned on for all players.
  
  Use Case 2
    Marking a player as found
      Pre-Condition: Player to be found must be active and within found range
      Description: The Admin/Seeker pushes a button next to a players name to indicate that he has been located.  
      Post-Condition:  Message is sent to hider asking for confirmation.
      Exceptions:  
      Player/host leaves match. 
      The player is not active.
      Player turns off location services
      
  Use Case 3
    Gameplay
      Pre-Condition: Game must be active
      Description: Hiders are visible to all participants and the seeker is not visible until they have been found,  the           seeker is not visible until a hider has been found. The seeker will invisible again in 7 seconds.
      Exceptions:  
      Player/host leaves match. 
      The player is not active.
      Player turns off location services
      
  Use Case 4
    End of Game
      Pre-Condition: Game must be active
      Description: the last hider has been found or the predetermined time has ended.
      Post-Condition:  once either condition is met the game will display a message to all user about the outcome.
      Seeker succeeded to find all players
      Seeker failed to find all players
      Exceptions:  
      Time limit not set.  
      Ends before set server time limit







