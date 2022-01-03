package com.lopl.melody.utils.settings;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {

  private static SettingsManager INSTANCE = null;
  public final Map<Long, GuildSettings> settings;

  public static SettingsManager getInstance(){
    if (INSTANCE == null) return new SettingsManager();
    return INSTANCE;
  }

  private SettingsManager() {
    settings = new HashMap<>();
    INSTANCE = this;
  }


  public GuildSettings getGuildSettings(Guild guild){
    long guildId = guild.getIdLong();
    GuildSettings setting = settings.get(guildId);

    if (setting == null) {
      setting = new SettingLoader().loadSettings(guild);
      settings.put(guildId, setting);
    }

    return setting;
  }
}
