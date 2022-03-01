package com.lopl.melody.audio.handler;

import com.lopl.melody.audio.util.BotRightsManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;

/**
 * This class schedules tracks for the audio player.
 */
public class TrackScheduler extends AudioEventAdapter {
  private final GuildAudioManager manager;
  private final AudioPlayer player;
  private final TrackQueue queue;
  private final ArrayList<OnTrackFinished> trackFinishedListeners;

  public static final int REPLAY_POSITION = 10; // in seconds

  public TrackScheduler(GuildAudioManager manager) {
    this.manager = manager;
    this.player = manager.player;
    this.queue = manager.queue;
    this.trackFinishedListeners = new ArrayList<>();
  }

  /**
   * Start the next track, stopping the current one if it is playing.
   */
  public boolean nextTrack() {
    AudioTrack previousTrack = player.getPlayingTrack();
    if (previousTrack != null) {
      for (OnTrackFinished otf : trackFinishedListeners)
        otf.trackSkipped(previousTrack, manager.guild);
    }
    // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
    // giving null to startTrack, which is a valid argument and will simply stop the player.
    AudioTrack next = queue.pop();
    if (next == null) {
      player.startTrack(null, false);
      BotRightsManager.of(manager.guild).requestMute();
      return false;
    } else {
      player.startTrack(next.makeClone(), false);
      return true;
    }
  }

  /**
   * Start the previous track and stop the current one if it is playing, if the current track is young.
   * If the current track is old, replay the current one.
   * A track is old after {@value #REPLAY_POSITION} seconds.
   */
  public boolean previousTrack(){
    AudioTrack current = player.getPlayingTrack();
    TrackHistory history = manager.history;
    if (current == null){
      AudioTrack track = history.pop();
      if (track == null) return false;
      player.startTrack(track.makeClone(), false);
      return true;
    }
    if (current.getPosition() < REPLAY_POSITION * 1000){
      for (OnTrackFinished otf : trackFinishedListeners)
        otf.trackBackSkipped(current, manager.guild);
      AudioTrack track = history.pop();
      if (track == null) return false;
      player.startTrack(track.makeClone(), false);
    } else {
      current.setPosition(0);
    }
    return true;
  }

  public void addListener(OnTrackFinished callback){
    trackFinishedListeners.add(callback);
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
    if (endReason.mayStartNext) {
      nextTrack();
    }
    // save track in history if current has finished
    if (endReason == AudioTrackEndReason.FINISHED){
      if (track != null) {
        for (OnTrackFinished otf : trackFinishedListeners)
          otf.trackFinished(track, manager.guild);
      }
    }
  }

  /**
   * This interface provides access to 
   */
  public interface OnTrackFinished {

    void trackSkipped(AudioTrack track, Guild guild);
    void trackBackSkipped(AudioTrack track, Guild guild);
    void trackFinished(AudioTrack track, Guild guild);

  }

}