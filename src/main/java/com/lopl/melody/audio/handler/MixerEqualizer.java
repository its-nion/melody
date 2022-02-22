package com.lopl.melody.audio.handler;

import com.lopl.melody.commands.music.Mixer;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.database.SQL;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class controls all the gains of the current player and acts like a equalizer.
 * It implements the functionality of the {@link Mixer}.
 */
public class MixerEqualizer extends EqualizerFactory {

  /**
   * these 3 integers store the multiplication value of the lows, the mids and the highs.
   * default is 0, max is 9 and min is -2
   */
  private int lows, mids, highs; // range of [-2, 9]
  /**
   * each MixerEqualizer requires the method {@link #setup(AudioPlayer)} to be called.
   * when this is done this boolean is true. When this is not yet done, the mixer will not work.
   */
  private boolean setup;

  /**
   * Constructor that enables switching of filters. I.e. this one here...
   */
  public MixerEqualizer() {
    PlayerManager.getInstance().audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
    setup = false;
    lows = mids = highs = 0;
  }

  /**
   * This will update the Equalizer at any time after the setup with the data values.
   * The 3 final float arrays define which gains are affected how by what multiplier.
   * If a setting of the mixer sounds odd you should tweak these values.
   */
  public void reapplyEqualizer() {
    final int GAINS = 15;
    //                             <------------LOW------------->  <--------MID-------->  <------HIGH------>
    final float[] BASS_MULTIPLIER = {0.01f, 0.07f, 0.08f, 0.05f, 0.3f, 0.01f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    //                                  <---------LOW------->  <-------------MID------------>  <--------HIGH------->
    final float[] MIDDLE_MULTIPLIER = {0f, 0f, 0f, 0f, 0.01f, 0.05f, 0.1f, 0.1f, 0.1f, 0.05f, 0.01f, 0f, 0f, 0f, 0f};
    //                                  <-------LOW------>  <--------MID-------->  <-------------HIGH------------>
    final float[] TREBLE_MULTIPLIER = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.01f, 0.03f, 0.05f, 0.08f, 0.1f, 0.1f};

    for (int i = 0; i < GAINS; i++) {
      float value = 0;
      value += BASS_MULTIPLIER[i] * lows;
      value += MIDDLE_MULTIPLIER[i] * mids;
      value += TREBLE_MULTIPLIER[i] * highs;
      setGain(i, value);
    }

  }

  /**
   * String getter of all gains
   * @return a String showing the value of all gains
   */
  public String getGains() {
    List<Float> gains = new ArrayList<>();
    for (int i = 0; i < 15; i++) {
      gains.add(getGain(i));
    }
    return Arrays.toString(gains.toArray());
  }

  /**
   * The magic setup method.
   * This will apply this class as the current mixer to the player
   * @param player the player
   */
  public void setup(AudioPlayer player) {
    if (setup) return;
    player.setFrameBufferDuration(500);
    player.setFilterFactory(this);
    setup = true;
  }

  /**
   * This returns a List of all possible values for lows, mids, highs.
   * All integer values between and including -2 and 9.
   * @return a list of integers
   */
  public List<Integer> getRange() {
    return List.of(9, 8, 7, 6, 5, 4, 3, 2, 1, 0, -1, -2);
  }

  public int getLows() {
    return lows;
  }

  public void setLows(int lows) {
    this.lows = minMax(-2, lows, 10);
    reapplyEqualizer();
  }

  public int getMids() {
    return mids;
  }

  public void setMids(int mids) {
    this.mids = minMax(-2, mids, 10);
    reapplyEqualizer();
  }

  public int getHighs() {
    return highs;
  }

  public void setHighs(int highs) {
    this.highs = minMax(-2, highs, 10);
    reapplyEqualizer();
  }

  private int minMax(int min, int actual, int max) {
    return Math.max(min, Math.min(actual, max));
  }

  public boolean isSetup() {
    return setup;
  }

  @Override
  public String toString() {
    return "[LOWS:" + getLows() + " MIDS:" + getMids() + " HIGHS:" + getHighs() + "]";
  }

  /**
   * This saves the current mixer to the database.
   * @param guild the guild this is saved for
   */
  public void save(Guild guild) {
    new SQL().execute("INSERT OR REPLACE INTO guild_music_mixer (guild_id, lows, mids, highs) VALUES (%s, %d, %d, %d)",
        guild.getId(), getLows(), getMids(), getHighs());
  }

  /**
   * This loads the saved mixer from the database or creates a new one if none is saved
   * @param guild the guild this is loaded for
   */
  public void load(Guild guild) {
    try {
      ResultSet resultSet = new SQL().query("SELECT * FROM guild_music_mixer WHERE guild_id=%s", guild.getId());
      if (resultSet.next()) {
        int lows = resultSet.getInt("lows");
        setLows(lows);
        int mids = resultSet.getInt("mids");
        setMids(mids);
        int highs = resultSet.getInt("highs");
        setHighs(highs);
      }
    } catch (SQLException sqle) {
      Logging.debug(getClass(), guild, null, "Loading of stored database mixer failed!");
    }
    // if nothing saved yet:
    save(guild);
  }
}
