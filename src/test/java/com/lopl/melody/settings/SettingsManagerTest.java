package com.lopl.melody.settings;

import com.lopl.melody.testutils.GuildCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link SettingsManager}
 */
class SettingsManagerTest {

  @Test
  void getInstance() {
    SettingsManager manager1 = SettingsManager.getInstance();
    SettingsManager manager2 = SettingsManager.getInstance();
    assertSame(manager1, manager2);
  }

  @Test
  void getGuildSettings() {
    SettingsManager manager = SettingsManager.getInstance();
    GuildSettings settings = manager.getGuildSettings(GuildCreator.create(123456789));
    assertNotNull(settings);
  }
}