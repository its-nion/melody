package com.lopl.melody.settings;

public abstract class SettingValue {
  protected final int data;

  public abstract String getValueRepresentation();


  public SettingValue(int player) {
    this.data = player;
  }

  public int getData(){
    return data;
  }
}
