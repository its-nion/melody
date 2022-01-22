package com.lopl.melody.settings;

import com.lopl.melody.testutils.GuildCreator;
import com.lopl.melody.utils.database.SQL;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test of {@link SettingSaver}
 */
class SettingSaverTest {

  @Test
  void saveSettings() {
    Guild guild = GuildCreator.create(123456789);
    GuildSettings settings = new GuildSettings(guild);
    SettingSaver settingSaver = new SettingSaver();
    settingSaver.saveSettings(guild, settings);
    ResultSet result = new SQL().query("SELECT * FROM guild_settings WHERE guild_id=123456789");
    assertNotNull(result);
  }

  @AfterEach
  void tearDown() {
    new SQL().execute("DELETE FROM guild_settings WHERE guild_id=123456789");
  }
}