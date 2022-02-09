package com.lopl.melody.settings.items;

import com.lopl.melody.commands.record.Record;
import com.lopl.melody.settings.Setting;
import com.lopl.melody.settings.SettingValue;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class controls whether Melody should automatically record the Voicechannel
 * it is joining. So on every join {@link Record#record(VoiceChannel)}
 * is executed.
 * The data holder of this Setting is {@link Value}.
 */
public class AutomaticRecording extends Setting<AutomaticRecording.Value> {

  /**
   * This returns the initial SettingValue.
   * It will be set as the current value for the setting in the constructor.
   * It returns the OFF state of the data holder, as Melody should not record automatically by default.
   *
   * @return {@link Value#off()}
   */
  @Override
  protected @NotNull Value getDefaultValue() {
    return Value.off();
  }

  /**
   * This returns all possible values for the setting.
   * Possible are 3 states : off, on, on and do not show
   *
   * @return a list (Nonnull and not Empty) containing all values of SettingValue
   */
  @Override
  public List<Value> getPossibilities() {
    return List.of(Value.off(), Value.record(), Value.recordNoMessage());
  }

  /**
   * This is called whenever a new data wants to be applied.
   * this will set the state to the new value and if the state is illegal, it will
   * be set to the default state.
   */
  @Override
  public void updateData(int data) {
    setValue(new Value(data));
    if (data <= 0 || data >= 4)
      setValue(getDefaultValue());
  }

  /**
   * @return The displayed Setting name.
   */
  @Override
  public String getName() {
    return "Automatic recording";
  }

  /**
   * This is the data holder class for {@link AutomaticRecording}.
   * It has 3 possible states: [{@link #OFF}, {@link #RECORD}, {@link #RECORD_NO_MESSAGE}].
   * The value is stored as an integer in {@link #data}.
   */
  public static class Value extends SettingValue {
    public static final int OFF = 1;
    public static final int RECORD = 2;
    public static final int RECORD_NO_MESSAGE = 3;

    /**
     * Constructor. public due to tests. Maybe change
     *
     * @param player the state of the player.
     */
    public Value(int player) {
      super(player);
    }

    /**
     * static constructor method for state {@link #OFF}.
     *
     * @return an instance of this class
     */
    public static Value off() {
      return new Value(OFF);
    }

    /**
     * static constructor method for state {@link #RECORD}.
     *
     * @return an instance of this class
     */
    public static Value record() {
      return new Value(RECORD);
    }

    /**
     * static constructor method for state {@link #RECORD_NO_MESSAGE}.
     *
     * @return an instance of this class
     */
    public static Value recordNoMessage() {
      return new Value(RECORD_NO_MESSAGE);
    }

    public boolean isOn() {
      return data == RECORD || data == RECORD_NO_MESSAGE;
    }

    public boolean isOff() {
      return data == OFF;
    }

    public boolean isWithMessage() {
      return data == RECORD;
    }

    /**
     * This returns the String, that is displayed, to show the current Value.
     *
     * @return a (short / one-word) String describing the Value
     */
    @Override
    public String getValueRepresentation() {
      if (getData() == OFF) return "Off";
      if (getData() == RECORD) return "Record";
      if (getData() == RECORD_NO_MESSAGE) return "Record without message";
      return "Not set";
    }

  }
}
