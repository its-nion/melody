package com.lopl.melody.audio.handler;

import com.lopl.melody.audio.util.AutomaticRequeue;
import com.lopl.melody.audio.util.AutomaticShuffle;
import com.lopl.melody.settings.items.AutomaticRecording;
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
  public final TrackHistory history;
  public final TrackQueue queue;

  /**
   * Mixer to boost channels
   */
  private final MixerEqualizer mixer;

  public final AutomaticRequeue requeuer;
  public final AutomaticShuffle shuffler;

  /**
   * Creates a player and a track scheduler.
   *
   * @param manager Audio player manager to use for creating the player.
   */
  public GuildAudioManager(Guild guild, AudioPlayerManager manager) {
    this.guild = guild;
    this.player = manager.createPlayer();
    this.queue = new TrackQueue(this);
    this.history = new TrackHistory();
    this.scheduler = new TrackScheduler(this);
    this.mixer = new MixerEqualizer();
    this.requeuer = new AutomaticRequeue(this);
    this.shuffler = new AutomaticShuffle(this);
    this.player.addListener(scheduler);
    this.scheduler.addListener(history);
    this.scheduler.addListener(queue);
    this.scheduler.addListener(requeuer);
    //this.ttsEngine = new TTSEngine();
  }

  /**
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public AudioSendHandler getSendHandler() {
    return new AudioSendMultiplexer(/*ttsEngine,*/ new MusicSendHandler(player));
  }

  public MixerEqualizer getMixer() {
    if (!mixer.isSetup()) {
      mixer.setup(player);
      mixer.load(guild);
    }
    return mixer;
  }
}
