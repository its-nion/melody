package com.lopl.melody.audio.provider.youtube;

import com.lopl.melody.utils.message.SavedMessage;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class YoutubeMessage extends SavedMessage {

  public AudioPlaylist[] playlists;
  public AudioTrack[] tracks;

  protected int index;

  public YoutubeMessage(Message message, AudioPlaylist[] playlists) {
    super(message);
    this.playlists = playlists;
    this.index = 0;
  }

  public YoutubeMessage(Message message, AudioTrack[] tracks) {
    super(message);
    this.tracks = tracks;
    this.index = 0;
  }

  public AudioPlaylist[] getPlaylists() {
    return playlists;
  }

  public AudioTrack[] getTracks() {
    return tracks;
  }

  public AudioTrack getCurrentTrack() {
    return tracks[index];
  }

  public AudioPlaylist getCurrentPlaylist() {
    return playlists[index];
  }

  public int getIndex() {
    return index;
  }

  public Guild getGuild() {
    return getMessage().getGuild();
  }

  public void show() {
    new YoutubeMessageDisplayer(this).show();
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
