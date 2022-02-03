package com.lopl.melody.audio.handler;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TrackHistory implements TrackScheduler.OnTrackFinished {

  private static final int MAX_HISTORY_SIZE = 20;
  private final List<AudioTrack> history;

  public TrackHistory() {
    this.history = new ArrayList<>();
  }

  @Override
  public void trackSkipped(AudioTrack track, Guild guild) {
    history.add(0, track);
    if (history.size() > MAX_HISTORY_SIZE)
      history.subList(0, MAX_HISTORY_SIZE);
  }

  @Override
  public void trackBackSkipped(AudioTrack track, Guild guild) {

  }

  @Override
  public void trackFinished(AudioTrack track, Guild guild) {
    history.add(0, track);
    if (history.size() > MAX_HISTORY_SIZE)
      history.subList(0, MAX_HISTORY_SIZE);
  }

  @Nullable
  public AudioTrack pop(){
    AudioTrack track = history.get(0);
    if (track == null) return null;
    history.remove(0);
    return track;
  }

  public List<AudioTrack> getHistory() {
    return history;
  }
}
