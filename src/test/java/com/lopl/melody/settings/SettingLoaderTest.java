package com.lopl.melody.settings;

import com.lopl.melody.testutils.GuildCreator;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SettingLoaderTest {

  @Test
  void loadSettings() {
    Guild guild = GuildCreator.create(123456789);
    GuildSettings settings = new GuildSettings(guild);
    SettingSaver settingSaver = new SettingSaver();
    settingSaver.saveSettings(guild, settings);
    SettingLoader settingLoader = new SettingLoader();
    GuildSettings loadedSettings = settingLoader.loadSettings(guild);
    assertNotNull(loadedSettings);
  }

  @Test
  void loadSettingsWithoutBeingInDatabase() {
    Guild guild = GuildCreator.create(123456789);
    SettingLoader settingLoader = new SettingLoader();
    GuildSettings loadedSettings = settingLoader.loadSettings(guild);
    assertNotNull(loadedSettings);
  }
}