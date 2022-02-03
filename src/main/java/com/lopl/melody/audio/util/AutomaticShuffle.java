package com.lopl.melody.audio.util;

import com.lopl.melody.audio.handler.GuildAudioManager;
import com.lopl.melody.audio.handler.TrackQueue;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

public class AutomaticShuffle implements TrackQueue.OnTrackQueued {

  private final GuildAudioManager manager;
  private boolean active;

  public AutomaticShuffle(GuildAudioManager manager) {
    this.manager = manager;
    this.active = false;
  }

  @Override
  public void onTrackQueuedNormal(AudioTrack track, int position, Guild guild) {
    if (active)
      manager.queue.shuffle();
  }

  @Override
  public void onTrackQueuedFirst(AudioTrack track, Guild guild) {

  }

  @Override
  public void onTrackQueuedAuto(AudioTrack track, Guild guild) {
    if (active)
      manager.queue.shuffleAuto();
  }

  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
  }

  public boolean isActive() {
    return active;
  }

}
