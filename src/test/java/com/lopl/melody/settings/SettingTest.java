package com.lopl.melody.settings;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link Setting}
 */
class SettingTest {

  @Test
  void getDatabaseName() {
    DatabaseNameSettingTest setting = new DatabaseNameSettingTest();
    String databaseName = setting.getDatabaseName();
    DatabaseNameSettingTestAlternative settingAlternative = new DatabaseNameSettingTestAlternative();
    String databaseNameAlternative = settingAlternative.getDatabaseName();
    assertEquals("database_name_setting_test", databaseName);
    assertEquals("custom_database_name", databaseNameAlternative);
  }

  @Test
  void getName(){
    DatabaseNameSettingTest setting = new DatabaseNameSettingTest();
    String name = setting.getName();
    DatabaseNameSettingTestAlternative settingAlternative = new DatabaseNameSettingTestAlternative();
    String nameAlternative = settingAlternative.getName();
    assertEquals("DatabaseNameSettingTest", name);
    assertEquals("Alternative", nameAlternative);
  }

  @Test
  void getValueRepresentation(){
    DatabaseNameSettingTest setting = new DatabaseNameSettingTest();
    String name = setting.getValueRepresentation();
    DatabaseNameSettingTestAlternative settingAlternative = new DatabaseNameSettingTestAlternative();
    String nameAlternative = settingAlternative.getValueRepresentation();
    assertNull(name);
    assertEquals("TestValue", nameAlternative);
  }

  private static class DatabaseNameSettingTest extends Setting<SettingValue>{
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

  private static class DatabaseNameSettingTestAlternative extends Setting<SettingValue>{

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