package com.lopl.melody.settings;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the manager class for all {@link Setting}s in all {@link Guild}s.
 * A Singleton Design pattern is implemented. See {@link #getInstance()} on how to instantiate this class.
 */
public class SettingsManager {

  /**
   * Reference of the only instance.
   */
  private static SettingsManager INSTANCE = null;

  /**
   * Map containing a guilds settings.
   * Stored with the id of the Guild as a long.
   */
  public final Map<Long, GuildSettings> settings;

  /**
   * Private constructor. Get a object of this class with {@link #getInstance()}
   */
  private SettingsManager() {
    settings = new HashMap<>();
    INSTANCE = this;
  }

  /**
   * Singleton instance getter method.
   * <p>
   * Example:
   * <pre>
   * SettingsManager settingsManager = SettingsManager.getInstance();
   * </pre>
   * @return the SettingsManager instance
   */
  public static SettingsManager getInstance() {
    if (INSTANCE == null) return new SettingsManager();
    return INSTANCE;
  }

  /**
   * Shortcut for {@link #getInstance()}.{@link #getGuildSettings(Guild)}
   * <p>
   * Example:
   * <pre>
   * GuildSettings guildSettings = SettingsManager.settingsOf(guild);
   * </pre>
   * @param guild the Guild, providing the ID
   * @return the GuildSettings object for the particular guild
   */
  public static GuildSettings settingsOf(Guild guild) {
    return getInstance().getGuildSettings(guild);
  }

  /**
   * This will search the {@link #settings} Map with the guilds ID for the settings of the guild.
   * If there is no cached GuildSettings yet, a new GuildSetting is Loaded with the
   * {@link SettingLoader} and saved in the map.
   * @param guild the Guild, providing the ID
   * @return the GuildSettings object for the particular guild
   */
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
