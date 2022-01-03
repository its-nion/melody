package com.lopl.melody.utils.settings;

import com.lopl.melody.commands.essentials.Settings;
import com.lopl.melody.utils.settings.items.DefaultMusicType;
import com.lopl.melody.utils.settings.items.MusicPlayerProvider;
import net.dv8tion.jda.api.entities.Guild;

public class SettingLoader {

  public GuildSettings loadSettings(Guild guild){
    //TODO: load settings from DB
    //if not in db:
    GuildSettings guildSettings = new GuildSettings();
    for (Setting setting : Settings.settings){
      setting.setValue(setting.getDefaultValue());
      guildSettings.register(setting.getClass(), setting);
    }
    return guildSettings;
  }

}
