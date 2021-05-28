<img src="https://github.com/necsii/melody/blob/main/.websrc/Melody_Banner.png" />

![GitHub release](https://img.shields.io/github/release/necsii/melody.svg)
![GitHub](https://img.shields.io/github/license/necsii/melody.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/necsii/melody.svg)
![Downloads](https://img.shields.io/github/downloads/necsii/melody/total.svg)

Melody is a visually pleasing and easy to use discord music bot. Using slash commands, Melody offers playing and searching for song's and playlist's in youtube, soundcloud and  more. Managing command permissions and saving them in a database is also possible. 

> Melody was made with ***Java and SQLite***

## Table of Contents

* [Core features](#core-features)
* [To do list](#to-do-list)
* [Command List](#command-list)
  * [General commands](#general)
  * [Music commands](#music)
* [Dependencies](#dependencies)

## Core features
* Visually pleasing and clean interface
* Simple yet functional commands
* Easy to run
* Thanks to Lavaplayer, Melody supports YouTube, SoundCloud, Bandcamp, Vimeo, Twitch streams

## To do list

Keep in mind that Melody is nowhere near finished. There may still be buggs and missing features. Im currently working to implement the following:

* [ ] Implementing buttons
* [ ] Adding loopqueue command
* [ ] Adding repeat command
* [ ] Adding showqueue feature
* [ ] Adding shuffle command
* [ ] Fixing volume command
* [ ] Fixing the database

## Command List

### General

Command | Description
----------------|----------------
`/info` | Displays infos, including an invite link and more
`/ping` | Displays the latency, in a fitting color

### Music

Command | Description
----------------|----------------
`/join` | Joins/Moves bot into members voice channel
`/disconnect` | Disconnects bot from the voice channel and resets player
`/play [song name/song url/playlist url]` | Adds/Searches for a song/playlist and adds it to the queue
`/stop` | Stops the player and clears queue
`/pause` | Pauses the player
`/resume` | Resumes the player
`/skip` `/skip [amount]` | Skips either one or [amount] songs
`/volume` `/volume [amount]` | Displays or changes the player volume

## Dependencies

Melody would't work without the following API's
* JDA
   * Version: **#1501**
   * [Github](https://github.com/DV8FromTheWorld/JDA)
* Lavaplayer
   * Version: *1.3.75*
   * [Github](https://github.com/sedmelluq/lavaplayer)
* HikariCP
   * Version: *4.0.3*
   * [Github](https://github.com/brettwooldridge/HikariCP)
* SQLite JDBC
   * Version: *3.14.2*
   * [Github](https://github.com/xerial/sqlite-jdbc)
