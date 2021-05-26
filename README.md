<img src="https://github.com/necsii/melody/blob/main/.websrc/Melody_Banner.png" />

![GitHub release](https://img.shields.io/github/release/necsii/melody.svg)
![GitHub](https://img.shields.io/github/license/necsii/melody.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/necsii/melody.svg)
![Downloads](https://img.shields.io/github/downloads/necsii/melody/total.svg)

Melody is a visually pleasing and easy to use discord music bot. Using slash commands, melody offers playing and searching for songs in youtube and soundcloud, playlists, queues and much more. Command permissions can managed and saved in a database.

Written with **Java** and a tad **SQLite**

## Table of Contents

* [Introduction](#discordbot-----)
* [Command List](#command-list)
  * [General commands](#general)
  * [Music commands](#music)
* [Dependencies](#dependencies)


## Command List

### General

Command | Description
----------------|----------------
`/info` | Displays infos, including invite link and much more
`/ping` | Displays the latency, in fitting color

### Music

Command | Description
----------------|----------------
`/join` | Joins/Moves into members voice channel
`/disconnect` | Disconnects from the voice channel and resets player
`/play [Song name/URL/Playlist]` | Adds/Searches for a song/playlist and adds it to the queue
`/stop` | Stops the player and clears queue
`/pause` | Pauses the player
`/resume` | Resumes the player
`/skip [amount]` | Skips either one or [amount] songs
`/volume [amount]` | Displays or changes the player volume

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
