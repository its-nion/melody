package com.lopl.melody.settings;

import com.lopl.melody.testutils.GuildCreator;
import com.lopl.melody.utils.database.SQL;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link GuildSettings}
 */
class GuildSettingsTest {

  @Test
  void register() {
    // arrange
    Guild guild = GuildCreator.create(123456789);
    Setting setting1 = new DatabaseNameSettingTest();
    Setting setting2 = new DatabaseNameSettingTestAlternative();
    GuildSettings guildSettings = new GuildSettings(guild);

    // act
    guildSettings.register(setting1.getClass(), setting1);
    guildSettings.register(setting2.getClass(), setting2);

    // assert
    assertTrue(guildSettings.getSettings().contains(setting1));
    assertTrue(guildSettings.getSettings().contains(setting2));
  }

  @Test
  void getSetting() {
    // arrange
    Guild guild = GuildCreator.create(123456789);
    Setting setting1 = new DatabaseNameSettingTest();
    GuildSettings guildSettings = new GuildSettings(guild);
    guildSettings.register(setting1.getClass(), setting1);

    // act
    // assert
    assertNotNull(guildSettings.getSetting(DatabaseNameSettingTest.class));
    assertNull(guildSettings.getSetting(DatabaseNameSettingTestAlternative.class));
  }

  @Test
  void getUnknownSetting() {
    // arrange
    Guild guild = GuildCreator.create(123456789);
    Setting setting1 = new DatabaseNameSettingTest();
    GuildSettings guildSettings = new GuildSettings(guild);
    guildSettings.register(setting1.getClass(), setting1);

    // act
    // assert
    assertNotNull(guildSettings.getUnknownSetting(DatabaseNameSettingTest.class));
    assertNull(guildSettings.getUnknownSetting(String.class));
  }

  @Test
  void getSettings() {
    // arrange
    Guild guild = GuildCreator.create(123456789);
    Setting setting1 = new DatabaseNameSettingTest();
    Setting setting2 = new DatabaseNameSettingTestAlternative();
    GuildSettings guildSettings = new GuildSettings(guild);
    guildSettings.register(setting1.getClass(), setting1);
    guildSettings.register(setting2.getClass(), setting2);

    // act
    // assert
    assertNotNull(guildSettings.getSettings());
    assertSame(2, guildSettings.getSettings().size());
  }

  @Test
  void notifyDatasetChanged() {
    // arrange
    Guild guild = GuildCreator.create(123456789);
    Setting setting1 = new DatabaseNameSettingTest();
    GuildSettings guildSettings = new GuildSettings(guild);
    guildSettings.register(setting1.getClass(), setting1);

    // act
    guildSettings.notifyDatasetChanged();

    // assert
    ResultSet result = new SQL().query("SELECT * FROM guild_settings WHERE guild_id=123456789");
    assertNotNull(result);
  }

  private static class DatabaseNameSettingTest extends Setting<SettingValue> {
    @Override
    protected @NotNull SettingValue getDefaultValue() {
      return new SettingValue(0) {
        @Override
        public String getValueRepresentation() {
          return null;
        }
      };
    }

    @Override
    public List<SettingValue> getPossibilities() {
      return null;
    }

    @Override
    public void updateData(int data) {

    }
  }

  private static class DatabaseNameSettingTestAlternative extends Setting<SettingValue> {

    public DatabaseNameSettingTestAlternative() {
      super.name = "Alternative";
    }

    @Override
    protected @NotNull SettingValue getDefaultValue() {
      return new SettingValue(0) {
        @Override
        public String getValueRepresentation() {
          return "TestValue";
        }
      };
    }

    @Override
    public List<SettingValue> getPossibilities() {
      return null;
    }

    @Override
    public void updateData(int data) {

    }

    @Override
    public String getDatabaseName() {
      return "custom_database_name";
    }
  }
}