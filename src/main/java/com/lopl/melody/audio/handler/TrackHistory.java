package com.lopl.melody.audio.handler;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps track of all tracks that have been played and have finished playing either by skipping or by the end of the track.
 * Tracks are ordered by the most recent track at the beginning.
 * Tracks are automatically added to the list, by the {@link TrackScheduler.OnTrackFinished} interface.
 * If you want to remove a track (i.e. if the user wants to back skip this can be achieved with the {@link #pop()} method.
 */
public class TrackHistory implements TrackScheduler.OnTrackFinished {

  private static final int MAX_HISTORY_SIZE = 20;
  private final List<AudioTrack> history;

  public TrackHistory() {
    this.history = new ArrayList<>();
  }

  /**
   * This method will get called from the {@link TrackScheduler} if a track has been skipped.
   * @param track the skipped track
   * @param guild the guild the track was skipped
   */
  @Override
  public void trackSkipped(AudioTrack track, Guild guild) {
    history.add(0, track);
    if (history.size() > MAX_HISTORY_SIZE)
      history.subList(0, MAX_HISTORY_SIZE);
  }

  /**
   * This method will get called from the {@link TrackScheduler} if a track has been skipped backwards.
   * Nothing to execute here
   * @param track the skipped track
   * @param guild the guild the track was skipped
   */
  @Override
  public void trackBackSkipped(AudioTrack track, Guild guild) {

  }

  /**
   * This method will get called from the {@link TrackScheduler} if a track has finished playing when the song is over.
   * @param track the finished track
   * @param guild the guild the track was played
   */
  @Override
  public void trackFinished(AudioTrack track, Guild guild) {
    history.add(0, track);
    if (history.size() > MAX_HISTORY_SIZE)
      history.subList(0, MAX_HISTORY_SIZE);
  }

  /**
   * This will return the most recent played song and remove it from the history.
   * @return the previous played track
   */
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
