package com.lopl.melody.settings;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SettingTest {

  @Test
  void getDatabaseName() {
    DatabaseNameSettingTest setting = new DatabaseNameSettingTest();
    String databaseName = setting.getDatabaseName();
    assertEquals("database_name_setting_test", databaseName);
  }

  private static class DatabaseNameSettingTest extends Setting<SettingValue>{
    @Override
    protected SettingValue getDefaultValue() {
      return null;
    }

    @Override
    public List<SettingValue> getPossibilities() {
      return null;
    }

    @Override
    public void updateData(int data) {

    }
  }
}