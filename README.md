![version](https://img.shields.io/badge/version-0.30-green)
![License](https://img.shields.io/github/license/necsii/melody.svg)
![coverage](https://img.shields.io/badge/coverage-80%25-yellowgreen)
![GitHub last commit](https://img.shields.io/github/last-commit/necsii/melody.svg)
![Conventional Commits](https://img.shields.io/badge/conventional%20commits-1.0.0-green.svg)
![Downloads](https://img.shields.io/github/downloads/necsii/melody/total.svg)
![Build Status](https://travis-ci.org/dwyl/esta.svg?branch=master)

<img src="https://github.com/necsii/melody/blob/main/.websrc/Melody_Banner.png" />

Melody is a visually pleasing and easy to use discord music bot. 
Using slash commands, buttons and selection-menus, Melody offers playing and searching for song's and playlist's in youtube and spotify.
To use melody you can host the bot yourself.

## Table of Contents

* [Core features](#core-features)
* [Command List](#command-list)
  * [General commands](#general)
  * [Music commands](#sound)
  * [Voice commands](#voice)
* [Dependencies](#dependencies)
* [Installation](#installation)
* [Documentation](#documentation)
* [Examples](#examples)

## Core features
* Visually pleasing and clean interface
* Simple yet functional commands
* Easy to run
* Thanks to Lavaplayer, Melody supports all your necessary music functionality
* Search for music with either Youtube or Spotify.
* Play music directly from Youtube, SoundCloud, Bandcamp and many more
* Create clips of your Voicechannel

## Command List

| Legend                 |
|------------------------|
| `{}` optional argument |
| `[]` required argument |
| `!`  disabled command  |

#### General

Command | Description
----------------|----------------
`/info` | Displays infos, including an invite link and more
`/ping` | Displays the latency, in a fitting color
`/help` | Displays a interactive help dialog
`/settings` | Change settings here. You can change your Music Searcher, the music search type and whether Melody automatically records in Voicechannels 
`/join` | Joins/Moves bot into members voice channel
`/disconnect` | Disconnects bot from the voice channel and resets player

#### Sound

Command | Description
----------------|----------------
`/play {song/playlist/user} [song name/url]` | Searches for a song/playlist/user or directly plays a link
`/player` | Shows a player to play/pause, skip and stop. tltr: Control the music 
`!/stop` | Stops the player and clears queue
`!/pause` | Pauses the player
`!/resume` | Resumes the player
`/skip {amount}` | Skips one or more songs
`/volume {amount}` | Displays or changes the player volume
`/queue` | Displays the current queue
`/mixer` | Modify the player mixer

#### Voice

Command | Description
----------------|----------------
`/record` | Starts recording of the Voicechannel
`/clip [time] {filename}` | Creates a clip of the last seconds and sends it to you

## Dependencies

**Melody is written in [Java](https://www.java.com/de/)**

From Java's Wikipedia Article:
> Java ist eine objektorientierte Programmiersprache und eine eingetragene Marke des Unternehmens Sun Microsystems, welches 2010 von Oracle aufgekauft wurde.

**Melody uses [SQLite](https://www.sqlite.org/index.html) as a backend database**

From SQLites Homepage:
> SQLite is a C-language library that implements a small, fast, self-contained, high-reliability, full-featured, SQL database engine. SQLite is the most used database engine in the world. SQLite is built into all mobile phones and most computers and comes bundled inside countless other applications that people use every day.


#### Melody wouldn't work without the following API's:

* [JDA](https://github.com/DV8FromTheWorld/JDA) 4.3.0_346
* [Jda-utilities](https://github.com/JDA-Applications/JDA-Utilities) 3.0.5
* [Lavaplayer](https://github.com/sedmelluq/lavaplayer) 1.3.75
* [HikariCP](https://github.com/brettwooldridge/HikariCP) 4.0.3
* [Sqlite-JDBC](https://github.com/xerial/sqlite-jdbc) 3.14.2
* [Spotify-web-api-java](https://github.com/spotify-web-api-java/spotify-web-api-java) 4.2.1
* [Logback-classic](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic) 1.2.8 
* [Lame](https://github.com/nwaldispuehl/java-lame) 3.98.4
   
## Installation
**TBA**

## Documentation
**TBA**

Java doc: TBA

## Examples
**TBA**
   
## Creators

* **Developer**
   * [Lukas Olesch](https://github.com/its-nion)
   * [Philipp Letschka](https://github.com/Phil0L)


* **Artists**
   * [Lukas Olesch](https://github.com/its-nion)


* alpha Tester
   * 

