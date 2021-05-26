<img src="https://github.com/necsii/melody/blob/main/.websrc/Melody_Banner.png" />

![GitHub release](https://img.shields.io/github/release/necsii/melody.svg)
![GitHub](https://img.shields.io/github/license/necsii/melody.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/necsii/melody.svg)
![Downloads](https://img.shields.io/github/downloads/necsii/melody/total.svg)

Melody is a visually pleasing and easy to use discord music bot written in Java. Using slash commands, melody offers playing and searching for songs in youtube and soundcloud, playlists, queues and much more. Command permissions can also be handled and are saved with SQLite.

## Table of Contents

* [Introduction](#discordbot-----)
* [Command List](#command-list)
  * [General commands](#general)
  * [Music commands](#music)
* [Dependencies](#dependencies)


## Command List

### General

Command | Description | Example
----------------|--------------|-------
`/info` | Displays infos, including invite link and much more | `/info`
`/ping` | Displays the latency, in fitting color | `/ping`

### Music

Command | Description | Example
----------------|--------------|-------
`/play [Song name/URL/Playlist]` | Joins the vc if not already inside, adds songs to queue | `/play Wonderful world`, `/play [asd](https://www.youtube.com/watch?v=dQw4w9WgXcQ)`
`/ping` | Displays the latency, in fitting color | `/ping`

## Dependencies

These are used in the project
* JDA
   * Version: **#1501**
   * [Github](https://github.com/DV8FromTheWorld/JDA)
* Lavaplayer
   * Version: *1.3.75*
   * [Github](https://github.com/sedmelluq/lavaplayer)
