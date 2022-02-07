package com.lopl.melody.audio.provider.youtube;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YoutubeTest {

  @Test
  void testPlaylistSearcher(){
    AudioPlaylist[] playlists = new Youtube().searchPlaylists("Songs 2018");
    assertNotNull(playlists);
    assertNotEquals(playlists.length, 0);
  }

  @Test
  void testUserSearcher(){
    AudioPlaylist[] playlists = new Youtube().searchUser("Daft Punk");
    String user = new Youtube().searchUserName("Daft Punk");
    assertNotNull(user);
    assertNotNull(playlists);
    assertNotEquals(playlists.length, 0);
  }

}