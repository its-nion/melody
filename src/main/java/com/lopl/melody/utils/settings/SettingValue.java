package com.lopl.melody.utils.settings;

import net.dv8tion.jda.api.interactions.components.Button;

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
