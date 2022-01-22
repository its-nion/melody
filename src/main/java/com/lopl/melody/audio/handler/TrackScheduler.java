package com.lopl.melody.audio.handler;

import com.lopl.melody.audio.util.BotRightsManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
  private final GuildAudioManager manager;
  private final AudioPlayer player;
  private final ArrayList<AudioTrack> queue;

  /**
   * @param player The audio player this scheduler uses
   */
  public TrackScheduler(GuildAudioManager manager, AudioPlayer player) {
    this.manager = manager;
    this.player = player;
    this.queue = new ArrayList<>();
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   *
   * @param track The track to play or add to queue.
   */
  public void queue(AudioTrack track) {
    // Calling startTrack with noInterrupt set to true will start the track only if nothing is currently playing. If
    // something is playing, it returns false and does nothing. In that case the player was already playing so this
    // track goes to the queue instead.
    if (!player.startTrack(track, true))
      queue.add(track);
    BotRightsManager.of(manager.guild).requestUnmute();
  }

  /**
   * Start the next track, stopping the current one if it is playing.
   */
  public boolean nextTrack() {
    // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
    // giving null to startTrack, which is a valid argument and will simply stop the player.
    if (queue.isEmpty()) {
      player.startTrack(null, false);
      BotRightsManager.of(manager.guild).requestMute();
      return false;
    } else {
      AudioTrack track = queue.get(0);
      player.stopTrack();
      player.startTrack(track, true);
      queue.remove(0);
      return true;
    }
  }

  public void shuffle() {
    Collections.shuffle(queue);
  }

  public ArrayList<AudioTrack> getQueue() {
    return queue;
  }

  public void clearQueue() {
    queue.clear();
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
    if (endReason.mayStartNext) {
      nextTrack();
    }
  }


}