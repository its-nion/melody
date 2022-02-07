package com.lopl.melody.settings;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {

  private static SettingsManager INSTANCE = null;
  public final Map<Long, GuildSettings> settings;

  private SettingsManager() {
    settings = new HashMap<>();
    INSTANCE = this;
  }

  public static SettingsManager getInstance() {
    if (INSTANCE == null) return new SettingsManager();
    return INSTANCE;
  }

  public static GuildSettings settingsOf(Guild guild) {
    return getInstance().getGuildSettings(guild);
  }

  public GuildSettings getGuildSettings(Guild guild) {
    long guildId = guild.getIdLong();
    GuildSettings setting = settings.get(guildId);

    if (setting == null) {
      setting = new SettingLoader().loadSettings(guild);
      settings.put(guildId, setting);
    }

    return setting;
  }
}
