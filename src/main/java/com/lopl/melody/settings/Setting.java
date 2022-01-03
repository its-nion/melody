package com.lopl.melody.settings;

import java.util.List;

public abstract class Setting<T extends SettingValue> {

  protected T value;
  protected String name;

  protected Setting() {
    setValue(getDefaultValue());
  }

  protected abstract T getDefaultValue();
  public abstract List<T> getPossibilities();
  public abstract void updateData(int data);

  public String getValueRepresentation() {
    return getValue().getValueRepresentation();
  }

  public void setValue(T v){
    value = v;
  }

  public T getValue(){
    return value;
  }

  public String getName() {
    if (name == null)
      return getClass().getSimpleName();
    return name;
  }
}
