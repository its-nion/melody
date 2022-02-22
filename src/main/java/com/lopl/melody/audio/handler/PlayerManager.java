package com.lopl.melody.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the manager of all audio players.
 * It keeps track of all players that are or were active in a guild.
 * Each Guild has its own instance of a {@link GuildAudioManager}.
 * Retrieve the GuildAudioManager like this:
 * <pre>
 * PlayerManager manager = PlayerManager.getInstance();
 * GuildAudioManager audioManager = manager.getGuildAudioManager(guild);
 * </pre>
 */
public class PlayerManager {
  private static PlayerManager INSTANCE;
  public final AudioPlayerManager audioPlayerManager;
  public final Map<Long, GuildAudioManager> guildAudioManagerMap;

  /**
   * Private singleton Constructor
   */
  private PlayerManager() {
    this.guildAudioManagerMap = new HashMap<>();
    this.audioPlayerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    AudioSourceManagers.registerLocalSource(audioPlayerManager);
  }

  /**
   * Getter for the singleton instance
   * @return the only instance of this class
   */
  public static synchronized PlayerManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PlayerManager();
    }
    return INSTANCE;
  }

  /**
   * This method will look for a GuildAudioManager in the {@link #guildAudioManagerMap} with the guild.
   * If there is one this is returned. If there is non, a new one is created and put in the map.
   * @param guild the guild with the guild id as a hash index
   * @return a cached or new GuildAudioManager
   */
  public synchronized GuildAudioManager getGuildAudioManager(Guild guild) {
    long guildId = guild.getIdLong();
    GuildAudioManager musicManager = guildAudioManagerMap.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildAudioManager(guild, audioPlayerManager);
      guildAudioManagerMap.put(guildId, musicManager);
    }
    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
    return musicManager;
  }

  // TODO move this method
  public void play(GuildAudioManager musicManager, AudioTrack track) {
    musicManager.queue.queue(track);
  }
}