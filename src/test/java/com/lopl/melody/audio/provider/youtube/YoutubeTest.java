package com.lopl.melody.audio.provider.youtube;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YoutubeTest {

  @Test
  void testPlaylistSearcher(){
    AudioPlaylist[] playlists = Youtube.getPlaylists("Songs 2018", new String[0]);
    assertNotNull(playlists);
    assertNotEquals(playlists.length, 0);
  }

}