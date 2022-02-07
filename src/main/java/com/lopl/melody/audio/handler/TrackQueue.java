package com.lopl.melody.audio.handler;

import com.lopl.melody.audio.util.BotRightsManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;

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

  @Nullable
  public AudioTrack pop() {
    if (!queue.isEmpty())
      return queue.remove(0);
    else if (!autoQueue.isEmpty())
      return autoQueue.remove(0);
    return null;
  }

  public void shuffle() {
    Collections.shuffle(queue);
  }

  public void shuffleAuto() {
    Collections.shuffle(autoQueue);
  }

  public ArrayList<AudioTrack> getQueue() {
    return queue;
  }

  public ArrayList<AudioTrack> getAutoQueue() {
    return autoQueue;
  }

  public void clearQueue() {
    queue.clear();
    autoQueue.clear();
  }

  @Override
  public void trackSkipped(AudioTrack track, Guild guild) {

  }

  @Override
  public void trackBackSkipped(AudioTrack track, Guild guild) {
    if (track != null)
      queueFront(track);
  }

  @Override
  public void trackFinished(AudioTrack track, Guild guild) {

  }

  public void addListener(OnTrackQueued callback){
    trackQueueListeners.add(callback);
  }

  public interface OnTrackQueued{
   void onTrackQueuedNormal(AudioTrack track, int position, Guild guild);
   void onTrackQueuedFirst(AudioTrack track, Guild guild);
   void onTrackQueuedAuto(AudioTrack track, Guild guild);
  }
}


