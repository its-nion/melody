package com.lopl.melody.settings.items;

import com.lopl.melody.commands.record.Record;
import com.lopl.melody.settings.Setting;
import com.lopl.melody.settings.SettingValue;
import net.dv8tion.jda.api.entities.VoiceChannel;
import com.lopl.melody.utils.json.JsonProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class controls what music platform should be used to search for tracks, playlists and more.
 * Currently supported platforms are Youtube and Spotify. Spotify requires a working API key.
 * This may be implemented free to use in the future.
 * The data holder of this Setting is {@link MusicPlayerProvider.Value}.
 */
public class MusicPlayerProvider extends Setting<MusicPlayerProvider.Value> {

  /**
   * This returns the initial SettingValue.
   * It will be set as the current value for the setting in the constructor.
   * It returns the YouTube state of the data holder, as Melody should can search
   * on YouTube without any key or account.
   *
   * @return {@link Value#youtube()}
   */
  @Override
  protected @NotNull Value getDefaultValue() {
    return Value.youtube();
  }

  /**
   * This returns all possible values for the setting.
   * Possible are 2 states : youtube, spotify
   *
   * @return a list (Nonnull and not Empty) containing all values of SettingValue
   */
  @Override
  public List<Value> getPossibilities() {
    if (JsonProperties.getProperties().getSpotifyApi() == null)
      return List.of(Value.youtube());
    return List.of(Value.youtube(), Value.spotify());
  }

  /**
   * This is called whenever a new data wants to be applied.
   * this will set the state to the new value and if the state is illegal, it will
   * be set to the default state.
   */
  @Override
  public void updateData(int data) {
    setValue(new Value(data));
    if (data <= 0 || data >= 3)
      setValue(getDefaultValue());
  }

  /**
   * @return The displayed Setting name.
   */
  @Override
  public String getName() {
    return "Music searcher";
  }

  /**
   * This is the data holder class for {@link MusicPlayerProvider}.
   * It has 3 possible states: [{@link #SPOTIFY}, {@link #YOUTUBE}].
   * The value is stored as an integer in {@link #data}.
   */
  public static class Value extends SettingValue {
    public static final int YOUTUBE = 1;
    public static final int SPOTIFY = 2;

    /**
     * Constructor. public due to tests. Maybe change
     *
     * @param player the state of the player.
     */
    public Value(int player) {
      super(player);
    }

    /**
     * static constructor method for state {@link #YOUTUBE}.
     *
     * @return an instance of this class
     */
    public static Value youtube() {
      return new Value(YOUTUBE);
    }

    /**
     * static constructor method for state {@link #SPOTIFY}.
     *
     * @return an instance of this class
     */
    public static Value spotify() {
      return new Value(SPOTIFY);
    }

    public boolean isYoutube() {
      return data == YOUTUBE;
    }

    public boolean isSpotify() {
      return data == SPOTIFY;
    }

    /**
     * This returns the String, that is displayed, to show the current Value.
     *
     * @return a (short / one-word) String describing the Value
     */
    @Override
    public String getValueRepresentation() {
      if (isSpotify()) return "Spotify";
      if (isYoutube()) return "Youtube";
      return "Not set";
    }

  }
}
