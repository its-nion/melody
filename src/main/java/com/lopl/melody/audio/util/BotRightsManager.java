package com.lopl.melody.audio.util;

import com.lopl.melody.audio.handler.AudioReceiveListener;
import com.lopl.melody.audio.handler.PlayerManager;
import com.lopl.melody.utils.Logging;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BotRightsManager extends ListenerAdapter {

  private static BotRightsManager INSTANCE;
  private static final HashMap<Long, GuildRightsHolder> RIGHTS = new HashMap<>();

  public BotRightsManager() {
    INSTANCE = this;
  }

  public static BotRightsManager getInstance(){
    if (INSTANCE == null)
      INSTANCE = new BotRightsManager();
    return INSTANCE;
  }

  public static GuildRightsHolder of(Guild guild) {
    long id = guild.getIdLong();
    if (RIGHTS.containsKey(id))
      return RIGHTS.get(id);
    GuildRightsHolder grh = new GuildRightsHolder(guild);
    RIGHTS.put(id, grh);
    return grh;
  }

  void removeData(Guild guild){
    RIGHTS.remove(guild.getIdLong());
  }

  @Override
  public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
    Member self = event.getGuild().getSelfMember();
    if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()) {
      GuildRightsHolder right = BotRightsManager.of(event.getGuild());
      self.deafen(right.isDeafened()).queue();
      self.mute(right.isMuted()).queue();
    }
  }

  @Override
  public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
    Member self = event.getGuild().getSelfMember();
    if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()) {
      GuildRightsHolder right = BotRightsManager.of(event.getGuild());
      self.deafen(right.isDeafened()).queue();
      self.mute(right.isMuted()).queue();
    }
  }

  public void onGuildVoiceRightsUpdate(GuildRightsHolder rightsHolder) {
    Member self = rightsHolder.guild.getSelfMember();
    if (self.getVoiceState() == null || !self.getVoiceState().inVoiceChannel()) {
      Logging.debug(getClass(), rightsHolder.guild, null, "Updating Voicechannel rights while not in channel. Action has no effect.");
      return;
    }
    self.deafen(rightsHolder.isDeafened()).queue();
    self.mute(rightsHolder.isMuted()).queue();
  }

  @Override
  public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
    if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()) {
      BotRightsManager.getInstance().removeData(event.getGuild());
    }
  }

  @Override
  public void onGuildVoiceMute(@NotNull GuildVoiceMuteEvent event) {
    if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()) {
      boolean nowMuted = event.isMuted();
      Logging.debug(getClass(), event.getGuild(), null, nowMuted ? "I am now muted" : "I am now longer muted!");
    }
  }

  @Override
  public void onGuildVoiceDeafen(@NotNull GuildVoiceDeafenEvent event) {
    if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()) {
      boolean nowMuted = event.isDeafened();
      Logging.debug(getClass(), event.getGuild(), null, nowMuted ? "I am now deafened" : "I am now longer deafened!");
    }
  }

  public static class GuildRightsHolder {
    private final Guild guild;
    private boolean deafened = true;
    private boolean muted = true;

    public GuildRightsHolder(Guild guild, boolean deafened, boolean muted) {
      this.guild = guild;
      this.deafened = deafened;
      this.muted = muted;
    }

    public GuildRightsHolder(Guild guild) {
      this.guild = guild;
    }

    public boolean isDeafened() {
      return deafened;
    }

    public boolean isMuted() {
      return muted;
    }

    public GuildRightsHolder requestDeafen() {
      // detect bad request
      AudioManager audioManager = guild.getAudioManager();
      if (audioManager.getReceivingHandler() != null)
        Logging.debug(getClass(), guild, null, "Deafened while still recording. This is bad user feedback and may stop recording, while it should.");

      // execute
      return setDeafen(true);
    }

    public GuildRightsHolder requestMute() {
      // detect bad request
      AudioPlayer player = PlayerManager.getInstance().getGuildAudioManager(guild).player;
      if (!player.isPaused() || player.getPlayingTrack() != null)
        Logging.debug(getClass(), guild, null, "Muted bot while it should play music. This is not good.");

      // execute
      return setMute(true);
    }

    public GuildRightsHolder requestUndeafen() {
      // detect bad request
      AudioManager audioManager = guild.getAudioManager();
      if (audioManager.getReceivingHandler() == null || !(audioManager.getReceivingHandler() instanceof AudioReceiveListener))
        Logging.debug(getClass(), guild, null, "Undeafened while not recording. This is bad user feedback");

      // execute
      return setDeafen(false);
    }

    public GuildRightsHolder requestUnmute() {
      // detect bad request
      AudioPlayer player = PlayerManager.getInstance().getGuildAudioManager(guild).player;
      if (player.isPaused() || player.getPlayingTrack() == null)
        Logging.debug(getClass(), guild, null, "Unmuted bot while it is not playing music. This is not good.");

      // execute
      return setMute(false);
    }

    public GuildRightsHolder setDeafen(boolean deafened) {
      this.deafened = deafened;
      BotRightsManager.getInstance().onGuildVoiceRightsUpdate(this);
      return this;
    }

    public GuildRightsHolder setMute(boolean muted) {
      this.muted = muted;
      BotRightsManager.getInstance().onGuildVoiceRightsUpdate(this);
      return this;
    }

  }

}
