package com.lopl.melody.settings;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class Setting<T extends SettingValue> {

  protected T value;
  protected String name;

  protected Setting() {
    setValue(getDefaultValue());
  }

  @Nonnull
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

  public String getDatabaseName(){
    String className = getClass().getSimpleName();
    StringBuilder lower_underscore = new StringBuilder();
    int index = 0;
    for (char c : className.toCharArray()){
      if (Character.isAlphabetic(c) && Character.isUpperCase(c) && index > 0){
        lower_underscore.append("_").append(Character.toLowerCase(c));
      }else{
        lower_underscore.append(Character.toLowerCase(c));
      }
      index++;
    }
    return lower_underscore.toString();
  }

  public String getName() {
    if (name == null)
      return getClass().getSimpleName();
    return name;
  }
}
