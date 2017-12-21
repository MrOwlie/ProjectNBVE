# ProjectNBVE
University Project
Snowball fight simulator

## Game
The game is based on a square grid.
Each square can either have snow or not(white / green).
If the square has snow you can rightclick while standing on it to make a snowball, this consumes the snow.
If the square has no snow it will spawn snow after 30 seconds.
A player can load 3 snowballs maximum.

A player has levels, each level adds 5HP and 1DMG, maximum level is 10.
Player information is stored in txt files on the server.
Information stored: username, password, location, #snowballs, level, exp

## Server
Keeps user records in txt documents.
Authenticates users
Handles simulation ( movement and snowball throwing / making etc..).


## Client
Enter username + password.
Connect and authenticate / create account
Enter world on saved location or spawn if first login.
WASD to walk, left mouse to throw, right mose to make snowball.


