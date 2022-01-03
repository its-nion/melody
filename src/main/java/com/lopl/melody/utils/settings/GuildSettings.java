package com.lopl.melody.utils.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildSettings {

  public final Map<Class<?>, Setting<?>> settings;

  public GuildSettings() {
    settings = new HashMap<>();
    //TODO load all default settings
  }

  public <T> void register(Class<T> tClass, Setting<?> setting){
    settings.put(tClass, setting);
  }

  public <T extends Setting<?>> T getSetting(Class<T> tClass){
    Setting<?> setting = settings.get(tClass);
    if (setting == null) return null;
    if (tClass.equals(setting.getClass())) return (T) setting;
    return null;
  }

  public Setting<SettingValue> getUnknownSetting(Class<?> tClass){
    Setting<?> setting = settings.get(tClass);
    if (setting == null) return null;
    if (tClass.equals(setting.getClass())) return (Setting<SettingValue>) setting;
    return null;
  }

  public List<Setting<?>> getSettings(){
    return List.copyOf(settings.values());
  }
}
