package com.lopl.melody.audio.util;

import com.lopl.melody.audio.handler.GuildAudioManager;
import com.lopl.melody.audio.handler.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

public class AutomaticRequeue implements TrackScheduler.OnTrackFinished {

  private final GuildAudioManager manager;
  private boolean active;

  public AutomaticRequeue(GuildAudioManager manager) {
    this.manager = manager;
    this.active = false;
  }

  @Override
  public void trackSkipped(AudioTrack track, Guild guild) {

  }

  @Override
  public void trackBackSkipped(AudioTrack track, Guild guild) {

  }

  @Override
  public void trackFinished(AudioTrack track, Guild guild) {
    if (active)
      manager.queue.queueInAuto(track);
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
