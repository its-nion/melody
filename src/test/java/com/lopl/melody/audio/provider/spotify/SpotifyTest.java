package com.lopl.melody.audio.provider.spotify;

import org.junit.jupiter.api.Test;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import static org.junit.jupiter.api.Assertions.*;

class SpotifyTest {

  @Test
  void searchUser() {
    String userName = new Spotify().searchUserName("Daft Punk");
    AlbumSimplified[] playlists = new Spotify().searchUser("Daft Punk");
    assertNotNull(userName);
    assertNotNull(playlists);

  }
}