package com.lopl.melody.settings;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This is the abstract class for a Setting.
 * Extend this to implement a setting.
 * Each setting needs a "Value" class that extends the {@link SettingValue} class.
 * This class is the Data holder of the setting and stores the state
 * <p>
 * Example:
 *
 * <pre>
 * public class MySetting extends Setting<MySettingValue>{
 *   [all the methods]
 *   public static class MySettingValue extends SettingValue{
 *     [all the methods]
 *   }
 * }
 * </pre>
 * @param <T> the link to the SettingValue class
 */
public abstract class Setting<T extends SettingValue> {

  /**
   * Value variable
   */
  private T value;

  /**
   * This is the name of the setting.
   * Set this in the constructor.
   */
  protected String name;

  /**
   * Constructor for the base Setting.
   * This will set the current value to the default value declared in {@link #getDefaultValue()}.
   * The super constructor (this) must be called in every Subclass
   */
  protected Setting() {
    setValue(getDefaultValue());
  }

  /**
   * This should return the initial SettingValue.
   * It will be set as the current value for the setting in the constructor.
   * @return a Nonnull value of SettingValue
   */
  @Nonnull
  protected abstract T getDefaultValue();

  /**
   * This should return all possible values for the setting.
   * You can make this a single liner with {@link List#of()}
   * <p>
   * Example:
   * <pre>
   * return List.of(myPossibleValue, myOtherValue);
   * </pre>
   * @return a list (Nonnull and not Empty) containing all values of SettingValue
   */
  public abstract List<T> getPossibilities();

  /**
   * This is called whenever a new data wants to be applied.
   * You should implement the Value change and check for impossible states.
   * <p>
   * Example:
   * <pre>
   * setValue(new MySettingValue(data));
   * if (data <= 0 || data >= 4)
   *   setValue(getDefaultValue());
   * </pre>
   * Other code can be added as you like
   * @param data a int containing the value representation
   */
  public abstract void updateData(int data);

  /**
   * Change this to change the appearance of the Value String.
   * You should consider overriding the {@link SettingValue#getValueRepresentation()} first.
   * @return a String describing the current value;
   */
  public String getValueRepresentation() {
    return getValue().getValueRepresentation();
  }

  /**
   * @return Getter for {@link #value}
   */
  public T getValue() {
    return value;
  }

  /**
   * @param v Setter for {@link #value}
   */
  public void setValue(T v) {
    value = v;
  }

  /**
   * This algorithm converts the simple class name from Upper-Camelcase
   * to Lower-Underscore-Case. The returned String is used to store the setting in a database.
   * <p>
   * Example:
   * <pre>
   * MyClassSetting -> my_class_setting
   * </pre>
   * Please do not Override this
   * @return Class name in lower_underscore_case
   */
  public String getDatabaseName() {
    String className = getClass().getSimpleName();
    StringBuilder lower_underscore = new StringBuilder();
    int index = 0;
    for (char c : className.toCharArray()) {
      if (Character.isAlphabetic(c) && Character.isUpperCase(c) && index > 0) {
        lower_underscore.append("_").append(Character.toLowerCase(c));
      } else {
        lower_underscore.append(Character.toLowerCase(c));
      }
      index++;
    }
    return lower_underscore.toString();
  }

  /**
   * Returns the displayed Setting name.
   * Can be set in the constructor with {@link #name}.
   * @return Display name of the Setting
   */
  public String getName() {
    if (name == null)
      return getClass().getSimpleName();
    return name;
  }
}
