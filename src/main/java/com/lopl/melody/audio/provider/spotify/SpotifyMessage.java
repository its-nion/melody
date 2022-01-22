package com.lopl.melody.audio.provider.spotify;

import com.lopl.melody.utils.message.SavedMessage;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class SpotifyMessage extends SavedMessage {

  public PlaylistSimplified[] playlists;
  public Track[] tracks;

  protected int index;

  public SpotifyMessage(Message message, PlaylistSimplified[] playlists) {
    super(message);
    this.playlists = playlists;
    this.index = 0;
  }

  public SpotifyMessage(Message message, Track[] tracks) {
    super(message);
    this.tracks = tracks;
    this.index = 0;
  }

  public PlaylistSimplified[] getPlaylists() {
    return playlists;
  }

  public Track[] getTracks() {
    return tracks;
  }

  public Track getCurrentTrack() {
    return tracks[index];
  }

  public PlaylistSimplified getCurrentPlaylist() {
    return playlists[index];
  }

  public int getIndex() {
    return index;
  }

  public Guild getGuild() {
    return getMessage().getGuild();
  }

  public void show() {
    new SpotifyMessageDisplayer(this).show();
  }

  public int size() {
    if (playlists != null)
      return playlists.length;
    else if (tracks != null)
      return tracks.length;
    return 0;
  }

  public void previous() {
    if (hasPrevious()) {
      index--;
      show();
    }
  }

  public boolean hasPrevious() {
    return index > 0;
  }

  public void next() {
    if (hasNext()) {
      index++;
      show();
    }
  }

  public boolean hasNext() {
    if (tracks != null)
      return index < tracks.length - 1;
    else if (playlists != null)
      return index < playlists.length - 1;
    else
      return false;
  }
}
