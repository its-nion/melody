package com.lopl.melody.settings;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the holder of every different {@link Setting}.
 * Every instance belongs to a specific {@link Guild}.
 * You can retrieve a GuildSettings object with:
 * <p>
 * Example:
 * <pre>
 * GuildSettings guildSettings = SettingsManager.settingsOf(guild);
 * </pre>
 */
public class GuildSettings {

  private final Map<Class<?>, Setting<?>> settings;
  private final Guild guild;

  /**
   * package-private Constructor for the {@link SettingLoader} to being able to create GuildSettings objects.
   * @param guild the belonging guild
   */
  GuildSettings(Guild guild) {
    this.settings = new HashMap<>();
    this.guild = guild;
  }

  /**
   * This method will put a Settings object or subclass into the {@link #settings} map.
   * @param tClass the class holding the setting
   * @param setting the setting itself
   * @param <T> the specific setting subclass generic
   */
  public <T> void register(Class<T> tClass, Setting<?> setting) {
    settings.put(tClass, setting);
  }

  /**
   * This will return the Setting object of the specified Setting class
   * @param tClass the class of the wanted object
   * @param <T> the generic of the wanted object
   * @return the wanted object
   */
  public <T extends Setting<?>> T getSetting(Class<T> tClass) {
    Setting<?> setting = settings.get(tClass);
    if (setting == null) return null;
    if (tClass.equals(setting.getClass())) return (T) setting;
    return null;
  }

  /**
   * This will return the Setting object of the specified Setting class.
   * The class doesn't has to be a specific Setting subclass to support classes instantiated from a classes String name.
   * @param tClass the class of the wanted object
   * @return the wanted object
   */
  public Setting<SettingValue> getUnknownSetting(Class<?> tClass) {
    Setting<?> setting = settings.get(tClass);
    if (setting == null) return null;
    if (tClass.equals(setting.getClass())) return (Setting<SettingValue>) setting;
    return null;
  }

  /**
   * This will act like a getter to return all registered settings
   * @return all settings
   */
  public List<Setting<?>> getSettings() {
    return List.copyOf(settings.values());
  }

  /**
   * Calling this method will trigger the {@link SettingSaver} to save this object to the database.
   */
  public void notifyDatasetChanged() {
    new SettingSaver().saveSettings(guild, this);
  }
}
