package com.lopl.melody.settings;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildSettings {

  private final Map<Class<?>, Setting<?>> settings;
  private final Guild guild;

  public GuildSettings(Guild guild) {
    this.settings = new HashMap<>();
    this.guild = guild;
  }

  public <T> void register(Class<T> tClass, Setting<?> setting) {
    settings.put(tClass, setting);
  }

  public <T extends Setting<?>> T getSetting(Class<T> tClass) {
    Setting<?> setting = settings.get(tClass);
    if (setting == null) return null;
    if (tClass.equals(setting.getClass())) return (T) setting;
    return null;
  }

  public Setting<SettingValue> getUnknownSetting(Class<?> tClass) {
    Setting<?> setting = settings.get(tClass);
    if (setting == null) return null;
    if (tClass.equals(setting.getClass())) return (Setting<SettingValue>) setting;
    return null;
  }

  public List<Setting<?>> getSettings() {
    return List.copyOf(settings.values());
  }

  public void notifyDatasetChanged() {
    new SettingSaver().saveSettings(guild, this);
  }
}
