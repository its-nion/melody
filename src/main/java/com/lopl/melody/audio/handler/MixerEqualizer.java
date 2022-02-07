package com.lopl.melody.audio.handler;

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

public class MixerEqualizer extends EqualizerFactory {

  private int lows, mids, highs; // range of [-2, 9]
  private boolean setup;

  public MixerEqualizer() {
    PlayerManager.getInstance().audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
    setup = false;
    lows = mids = highs = 0;
  }

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

  public String getGains() {
    List<Float> gains = new ArrayList<>();
    for (int i = 0; i < 15; i++) {
      gains.add(getGain(i));
    }
    return Arrays.toString(gains.toArray());
  }

  public void setup(AudioPlayer player) {
    if (setup) return;
    player.setFrameBufferDuration(500);
    player.setFilterFactory(this);
    setup = true;
  }

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

  public void save(Guild guild) {
    new SQL().execute("INSERT OR REPLACE INTO guild_music_mixer (guild_id, lows, mids, highs) VALUES (%s, %d, %d, %d)",
        guild.getId(), getLows(), getMids(), getHighs());
  }

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
