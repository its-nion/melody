package com.lopl.melody.settings.items;

import com.lopl.melody.settings.Setting;
import com.lopl.melody.settings.SettingValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class controls what Music type Melody should search for if non is specified.
 * Possible types are: [track, playlist, user].
 * The data holder of this Setting is {@link DefaultMusicType.Value}.
 */
public class DefaultMusicType extends Setting<DefaultMusicType.Value> {

  /**
   * This returns the initial SettingValue.
   * It will be set as the current value for the setting in the constructor.
   * It returns the Track state of the data holder.
   * @return {@link Value#track()}
   */
  @Override
  protected @NotNull Value getDefaultValue() {
    return Value.track();
  }

  /**
   * This returns all possible values for the setting.
   * Possible are 3 states : track, playlist, user
   * @return a list (Nonnull and not Empty) containing all values of SettingValue
   */
  @Override
  public List<Value> getPossibilities() {
    return List.of(Value.track(), Value.playlist(), Value.user());
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
    return "Default music type";
  }

  /**
   * This is the data holder class for {@link DefaultMusicType}.
   * It has 3 possible states: [{@link #TRACK}, {@link #PLAYLIST}, {@link #USER}].
   * The value is stored as an integer in {@link #data}.
   */
  public static class Value extends SettingValue {
    public static final int TRACK = 1;
    public static final int PLAYLIST = 2;
    public static final int USER = 3;

    /**
     * Constructor. public due to tests. Maybe change
     *
     * @param player the state of the player.
     */
    public Value(int player) {
      super(player);
    }

    /**
     * static constructor method for state {@link #TRACK}.
     *
     * @return an instance of this class
     */
    public static Value track() {
      return new Value(TRACK);
    }

    /**
     * static constructor method for state {@link #PLAYLIST}.
     *
     * @return an instance of this class
     */
    public static Value playlist() {
      return new Value(PLAYLIST);
    }

    /**
     * static constructor method for state {@link #USER}.
     *
     * @return an instance of this class
     */
    public static Value user() {
      return new Value(USER);
    }

    public boolean isTrack() {
      return data == TRACK;
    }

    public boolean isPlaylist() {
      return data == PLAYLIST;
    }

    public boolean isUser() {
      return data == USER;
    }

    /**
     * This returns the String, that is displayed, to show the current Value.
     *
     * @return a (short / one-word) String describing the Value
     */
    @Override
    public String getValueRepresentation() {
      if (isTrack()) return "Tracks";
      if (isPlaylist()) return "Playlists";
      if (isUser()) return "Users Playlists";
      return "Not set";
    }

  }
}
