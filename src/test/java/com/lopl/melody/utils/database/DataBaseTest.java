package com.lopl.melody.utils.database;

import com.lopl.melody.settings.GuildSettings;
import com.lopl.melody.settings.SettingLoader;
import com.lopl.melody.settings.SettingSaver;
import com.lopl.melody.settings.items.DefaultMusicType;
import com.lopl.melody.testutils.GuildCreator;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseTest {

  @Test
  void dataBaseCorrectSetup() {
    // arrange

    // act
    ColumnLoader<String> loader = new ColumnLoader<>(new SQL().query("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;"));
    List<String> tables = loader.every(resultSet -> resultSet.getString("name"));

    // assert
    assertNotNull(tables);
    assertFalse(tables.isEmpty());
    assertTrue(tables.contains("guild_settings"));
  }

  @Test
  void settingDatabase() {
    //arrange
    Guild guild = GuildCreator.create(1234567890);

    // act
    GuildSettings guildSettings = new SettingLoader().loadSettings(guild);
    guildSettings.getSetting(DefaultMusicType.class).setValue(DefaultMusicType.Value.user());
    new SettingSaver().saveSettings(guild, guildSettings);
    guildSettings = new SettingLoader().loadSettings(guild);

    // assert
    assertEquals(DefaultMusicType.Value.user().getData(), guildSettings.getSetting(DefaultMusicType.class).getValue().getData());
    //TODO: better checks for settings table setup
    // - table update
    // - setting added
    // - setting removed
    // - setting renamed
  }

  @AfterEach
  void tearDown() {
    new SQL().execute("DELETE FROM guild_settings WHERE guild_id=1234567890");
  }
}