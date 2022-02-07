package com.lopl.melody.audio.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
  private static PlayerManager INSTANCE;
  public final AudioPlayerManager audioPlayerManager;
  public final Map<Long, GuildAudioManager> guildAudioManagerMap;

  private PlayerManager() {
    this.guildAudioManagerMap = new HashMap<>();
    this.audioPlayerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    AudioSourceManagers.registerLocalSource(audioPlayerManager);
  }

  public static synchronized PlayerManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PlayerManager();
    }
    return INSTANCE;
  }

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

  public void play(GuildAudioManager musicManager, AudioTrack track) {
    musicManager.queue.queue(track);
  }
}