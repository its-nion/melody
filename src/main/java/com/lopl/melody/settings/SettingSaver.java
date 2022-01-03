package com.lopl.melody.settings;

import com.lopl.melody.settings.items.DefaultMusicType;
import com.lopl.melody.settings.items.MusicPlayerProvider;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.database.SQL;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.TestOnly;

public class SettingSaver {

  public void saveSettings(Guild guild, GuildSettings settings) {
    boolean result = new SQL().execute(
        "INSERT OR REPLACE INTO guild_settings (guild_id, music_player_provider, default_music_type) VALUES (%s, %d, %d)",
        guild.getId(), settings.getSetting(MusicPlayerProvider.class).value.data, settings.getSetting(DefaultMusicType.class).value.data);
    if (!result) Logging.debug(getClass(), guild, null, "Saving guild settings failed!");
  }

  @TestOnly
  public void saveSettings(long guild, GuildSettings settings) {
    boolean result = new SQL().execute(
        "INSERT OR REPLACE INTO guild_settings (guild_id, music_player_provider, default_music_type) VALUES (%s, %d, %d)",
        guild, settings.getSetting(MusicPlayerProvider.class).value.data, settings.getSetting(DefaultMusicType.class).value.data);
    if (!result) Logging.debug(getClass(), null, null, "Saving guild settings failed!");
  }
}
