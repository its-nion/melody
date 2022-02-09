package com.lopl.melody.settings;

import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.database.SQL;
import net.dv8tion.jda.api.entities.Guild;

import java.util.stream.Collectors;

public class SettingSaver {

  /**
   * This saves a given GuildSetting for the given Guild to the database.
   * The database table is "guild_settings".
   * <p>
   * Example:
   * <pre>
   * new SettingSaver().saveSettings(guild, settings);
   * </pre>
   * @param guild the guild the settings are from
   * @param settings the guilds settings
   */
  public void saveSettings(Guild guild, GuildSettings settings) {
    boolean result = new SQL().execute(
        "INSERT OR REPLACE INTO guild_settings (guild_id, %s) VALUES (%s, %s)",
        getDatabaseFields(settings), guild.getId(), getDatabaseValues(settings));
    if (!result) Logging.debug(getClass(), guild, null, "Saving guild settings failed!");
  }

  private String getDatabaseFields(GuildSettings guildSettings) {
    return guildSettings.getSettings().stream().map(Setting::getDatabaseName).collect(Collectors.joining(", "));
  }

  private String getDatabaseValues(GuildSettings guildSettings) {
    return guildSettings.getSettings().stream().map(s -> String.valueOf(s.getValue().data)).collect(Collectors.joining(", "));
  }
}
