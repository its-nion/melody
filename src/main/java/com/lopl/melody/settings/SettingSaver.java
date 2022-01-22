package com.lopl.melody.settings;

import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.database.SQL;
import net.dv8tion.jda.api.entities.Guild;

import java.util.stream.Collectors;

public class SettingSaver {

  public void saveSettings(Guild guild, GuildSettings settings) {
    boolean result = new SQL().execute(
        "INSERT OR REPLACE INTO guild_settings (guild_id, %s) VALUES (%s, %S)",
        getDatabaseFields(settings), guild.getId(), getDatabaseValues(settings));
    if (!result) Logging.debug(getClass(), guild, null, "Saving guild settings failed!");
  }

  private String getDatabaseFields(GuildSettings guildSettings){
    return guildSettings.getSettings().stream().map(Setting::getDatabaseName).collect(Collectors.joining(", "));
  }

  private String getDatabaseValues(GuildSettings guildSettings){
    return guildSettings.getSettings().stream().map(s -> String.valueOf(s.getValue().data)).collect(Collectors.joining(", "));
  }
}
