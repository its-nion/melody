package com.lopl.melody.settings;

import com.lopl.melody.commands.essentials.Settings;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.database.SQL;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingLoader {

  /**
   * This loads a given GuildSetting for the given Guild from the database.
   * The database table is "guild_settings".
   * if the database does not contain settings for the guild yet, a new setting is created and stored afterwards.
   * <p>
   * Example:
   * <pre>
   * new SettingLoader().loadSettings(guild, settings);
   * </pre>
   * @param guild the settings guild
   */
  public GuildSettings loadSettings(Guild guild) {
    try {
      ResultSet resultSet = new SQL().query("SELECT * FROM guild_settings WHERE guild_id=%s", guild.getId());
      if (resultSet.next()) {
        GuildSettings guildSettings = new GuildSettings(guild);
        for (Setting<?> setting : Settings.settings) {
          int storedValue = resultSet.getInt(setting.getDatabaseName());
          setting.updateData(storedValue);
          guildSettings.register(setting.getClass(), setting);
        }
        return guildSettings;
      }
    } catch (SQLException ignored) {
      Logging.debug(getClass(), guild, null, "Loading of stored database settings failed!");
    }
    //if not in db:
    GuildSettings guildSettings = new GuildSettings(guild);
    for (Setting setting : Settings.settings) {
      setting.setValue(setting.getDefaultValue());
      guildSettings.register(setting.getClass(), setting);
    }
    new SettingSaver().saveSettings(guild, guildSettings);
    return guildSettings;
  }
}
