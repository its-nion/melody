package com.lopl.melody.settings;

/**
 * This is the abstract class for a SettingValue.
 * Extend this to implement a data holder for a {@link Setting}.
 * Each setting needs a "Value" class.
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
 */
public abstract class SettingValue {

  /**
   * This int will represent the state of the value. It is Immutable,
   * so every data change will result in a new SettingValue object.
   */
  protected final int data;

  /**
   * Constructor for a SettingValue object.
   * You can define static Creation methods for all possible states.
   * <p>
   * Example:
   * <pre>
   * public static MySettingValue state1(){
   *   return new MySettingValue(1);
   * }
   *
   * public static MySettingValue state2(){
   *   return new MySettingValue(2);
   * }
   *
   * private MySettingValue(int value){
   *   super(value);
   * }
   * </pre>
   * @param player int representation
   */
  public SettingValue(int player) {
    this.data = player;
  }

  /**
   * This returns the String, that is displayed, to show the current Value.
   * @return a (short / one-word) String describing the Value
   */
  public abstract String getValueRepresentation();

  /**
   * @return Getter for {@link #data}
   */
  public int getData() {
    return data;
  }
}
