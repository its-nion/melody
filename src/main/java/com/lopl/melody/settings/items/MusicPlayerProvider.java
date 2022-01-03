package com.lopl.melody.settings.items;

import com.lopl.melody.settings.Setting;
import com.lopl.melody.settings.SettingValue;
import java.util.List;

public class MusicPlayerProvider extends Setting<MusicPlayerProvider.Value> {

  @Override
  protected Value getDefaultValue() {
    return Value.spotify();
  }

  @Override
  public List<Value> getPossibilities() {
    return List.of(Value.youtube(), Value.spotify());
  }

  @Override
  public void updateData(int data) {
    setValue(new Value(data));
  }

  public static class Value extends SettingValue{
    public static final int YOUTUBE = 1;
    public static final int SPOTIFY = 2;

    public Value(int player) {
      super(player);
    }

    public static Value youtube(){
      return new Value(YOUTUBE);
    }

    public static Value spotify(){
      return new Value(SPOTIFY);
    }

    public boolean isYoutube(){
      return data == YOUTUBE;
    }

    public boolean isSpotify(){
      return data == SPOTIFY;
    }

    @Override
    public String getValueRepresentation() {
      if (isSpotify()) return "Spotify";
      if (isYoutube()) return "Youtube";
      return "Not set";
    }

  }
}
