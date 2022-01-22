package com.lopl.melody.settings.items;

import com.lopl.melody.settings.Setting;
import com.lopl.melody.settings.SettingValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutomaticRecording extends Setting<AutomaticRecording.Value> {
  @Override
  protected @NotNull Value getDefaultValue() {
    return Value.off();
  }

  @Override
  public List<Value> getPossibilities() {
    return List.of(Value.off(), Value.record(), Value.recordNoMessage());
  }

  @Override
  public void updateData(int data) {
    setValue(new Value(data));
    if (data <= 0 || data >= 4)
      setValue(getDefaultValue());
  }

  @Override
  public String getName() {
    return "Automatic recording";
  }

  public static class Value extends SettingValue {
    public static final int OFF = 1;
    public static final int RECORD = 2;
    public static final int RECORD_NO_MESSAGE = 3;

    public Value(int player) {
      super(player);
    }

    public static Value off(){
      return new Value(OFF);
    }

    public static Value record(){
      return new Value(RECORD);
    }

    public static Value recordNoMessage(){
      return new Value(RECORD_NO_MESSAGE);
    }

    public boolean isOn(){
      return data == RECORD || data == RECORD_NO_MESSAGE;
    }

    public boolean isOff(){
      return data == OFF;
    }

    public boolean isWithMessage(){
      return data == RECORD;
    }

    @Override
    public String getValueRepresentation() {
      if (getData() == OFF) return "Off";
      if (getData() == RECORD) return "Record";
      if (getData() == RECORD_NO_MESSAGE) return "Record without message";
      return "Not set";
    }

  }
}
