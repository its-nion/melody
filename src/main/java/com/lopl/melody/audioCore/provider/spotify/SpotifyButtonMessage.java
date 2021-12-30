package com.lopl.melody.audioCore.provider.spotify;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.entities.Message;

public class SpotifyButtonMessage extends SpotifyMessage {

  public SpotifyButtonMessage(Message message, PlaylistSimplified[] playlists) {
    super(message, playlists);
  }

  public SpotifyButtonMessage(Message message, Track[] tracks) {
    super(message, tracks);
  }

  @Override
  public void show() {
    new SpotifyMessageDisplayer(this).showNoReaction();
  }

  public void previous(){
    if (hasPrevious()) {
      super.index--;
      show();
    }
  }

  public boolean hasPrevious(){
    return super.index > 0;
  }

  public void next(){
    if (hasNext()) {
      super.index++;
      show();
    }
  }

  public boolean hasNext(){
    if (super.tracks != null)
      return super.index < super.tracks.length -1;
    else if (super.playlists != null)
      return super.index < super.playlists.length -1;
    else
      return false;
  }
}
