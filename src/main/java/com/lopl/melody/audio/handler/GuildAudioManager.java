package com.lopl.melody.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildAudioManager {

  public final Guild guild;

  /**
   * Audio player for the guild.
   */
  public final AudioPlayer player;

  /**
   * Track scheduler for the player.
   */
  public final TrackScheduler scheduler;

  /**
   * Creates a player and a track scheduler.
   *
   * @param manager Audio player manager to use for creating the player.
   */
  public GuildAudioManager(Guild guild, AudioPlayerManager manager) {
    this.guild = guild;
    this.player = manager.createPlayer();
    this.scheduler = new TrackScheduler(this, player);
    this.player.addListener(scheduler);
    //this.ttsEngine = new TTSEngine();
  }

  /**
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public AudioSendHandler getSendHandler() {
    return new AudioSendMultiplexer(/*ttsEngine,*/ new MusicSendHandler(player));
  }
}
