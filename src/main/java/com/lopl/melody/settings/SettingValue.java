package com.lopl.melody.settings;

public abstract class SettingValue {
  protected final int data;

  public SettingValue(int player) {
    this.data = player;
  }

  public abstract String getValueRepresentation();

  public int getData() {
    return data;
  }
}
