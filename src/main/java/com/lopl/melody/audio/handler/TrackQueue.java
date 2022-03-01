package com.lopl.melody.audio.handler;

import com.lopl.melody.audio.util.AutomaticRequeue;
import com.lopl.melody.audio.util.BotRightsManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class manages all tracks that are queue to be played. This way no one has to search for a new song if the last one is over.
 * The class has the 'normal' {@link #queue}, that contains all songs that are queued from the user.
 * The {@link #autoQueue} contains tracks that are queued by code. i.e. from the {@link AutomaticRequeue}.
 *
 */
public class TrackQueue implements TrackScheduler.OnTrackFinished {

  private final GuildAudioManager manager;
  private final ArrayList<AudioTrack> queue;
  private final ArrayList<AudioTrack> autoQueue;
  private final ArrayList<OnTrackQueued> trackQueueListeners;

  public TrackQueue(GuildAudioManager manager) {
    this.manager = manager;
    this.queue = new ArrayList<>();
    this.autoQueue = new ArrayList<>();
    this.trackQueueListeners = new ArrayList<>();
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
    if (!manager.player.startTrack(track.makeClone(), true)) {
      queue.add(track);
      for (OnTrackQueued tql : trackQueueListeners)
        tql.onTrackQueuedNormal(track, queue.size()-1, manager.guild);
    }
    BotRightsManager.of(manager.guild).requestUnmute();
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   * If the queue already contains tracks the track is added at the front
   * @param track The track to play or add to queue.
   */
  private void queueFront(AudioTrack track) {
    // Calling startTrack with noInterrupt set to true will start the track only if nothing is currently playing. If
    // something is playing, it returns false and does nothing. In that case the player was already playing so this
    // track goes to the queue instead.
    if (!manager.player.startTrack(track.makeClone(), true)) {
      queue.add(0, track);
      for (OnTrackQueued tql : trackQueueListeners)
        tql.onTrackQueuedFirst(track, manager.guild);
    }
    BotRightsManager.of(manager.guild).requestUnmute();
  }

  /**
   * Add the next track to autoQueue or play right away if nothing is in the queue.
   *
   * @param track The track to play or add to queue.
   */
  public void queueInAuto(AudioTrack track) {
    // Calling startTrack with noInterrupt set to true will start the track only if nothing is currently playing. If
    // something is playing, it returns false and does nothing. In that case the player was already playing so this
    // track goes to the auto queue instead.
    if (!manager.player.startTrack(track.makeClone(), true)) {
      autoQueue.add(track);
      for (OnTrackQueued tql : trackQueueListeners)
        tql.onTrackQueuedAuto(track, manager.guild);
    }
    BotRightsManager.of(manager.guild).requestUnmute();
  }

  /**
   * This will return the track that should be played next and remove it from the correct queue.
   * @return the next track
   */
  @Nullable
  public AudioTrack pop() {
    if (!queue.isEmpty())
      return queue.remove(0);
    else if (!autoQueue.isEmpty())
      return autoQueue.remove(0);
    return null;
  }

  /**
   * This shuffles the current user selected queue.
   */
  public void shuffle() {
    Collections.shuffle(queue);
  }

  /**
   * This shuffles the autoQueue to generate some variation
   */
  public void shuffleAuto() {
    Collections.shuffle(autoQueue);
  }

  public ArrayList<AudioTrack> getQueue() {
    return queue;
  }

  public ArrayList<AudioTrack> getAutoQueue() {
    return autoQueue;
  }

  /**
   * This clears/deletes the current queues.
   * After the currently playing song there will be nothing left playing.
   */
  public void clearQueue() {
    queue.clear();
    autoQueue.clear();
  }

  /**
   * This method will get called from the {@link TrackScheduler} if a track has been skipped.
   * Nothing to execute here
   * @param track the skipped track
   * @param guild the guild the track was skipped
   */
  @Override
  public void trackSkipped(AudioTrack track, Guild guild) {

  }

  /**
   * This method will get called from the {@link TrackScheduler} if a track has been skipped backwards.
   * @param track the skipped track
   * @param guild the guild the track was skipped
   */
  @Override
  public void trackBackSkipped(AudioTrack track, Guild guild) {
    if (track != null)
      queueFront(track);
  }

  /**
   * This method will get called from the {@link TrackScheduler} if a track has finished playing when the song is over.
   * Nothing to execute here
   * @param track the finished track
   * @param guild the guild the track was played
   */
  @Override
  public void trackFinished(AudioTrack track, Guild guild) {

  }

  /**
   * You can add a listener here to listen for songs that are getting queued
   * @param callback your listener
   */
  public void addListener(OnTrackQueued callback){
    trackQueueListeners.add(callback);
  }

  /**
   * This interface provides access to tracks that are getting queued.
   * @see #addListener(OnTrackQueued)
   */
  public interface OnTrackQueued{
   void onTrackQueuedNormal(AudioTrack track, int position, Guild guild);
   void onTrackQueuedFirst(AudioTrack track, Guild guild);
   void onTrackQueuedAuto(AudioTrack track, Guild guild);
  }
}


