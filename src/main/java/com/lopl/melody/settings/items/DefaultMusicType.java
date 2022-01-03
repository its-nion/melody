package com.lopl.melody.settings.items;

import com.lopl.melody.settings.Setting;
import com.lopl.melody.settings.SettingValue;

import java.util.List;

public class DefaultMusicType extends Setting<DefaultMusicType.Value> {

  @Override
  protected Value getDefaultValue() {
    return Value.track();
  }

  @Override
  public List<Value> getPossibilities() {
    return List.of(Value.track(), Value.playlist(), Value.user());
  }

  @Override
  public void updateData(int data) {
    setValue(new Value(data));
  }

  public static class Value extends SettingValue {
    public static final int TRACK = 1;
    public static final int PLAYLIST = 2;
    public static final int USER = 3;

    public Value(int player) {
      super(player);
    }

    public static Value track(){
      return new Value(TRACK);
    }

    public static Value playlist(){
      return new Value(PLAYLIST);
    }

    public static Value user(){
      return new Value(USER);
    }

    public boolean isTrack(){
      return data == TRACK;
    }

    public boolean isPlaylist(){
      return data == PLAYLIST;
    }

    public boolean isUser(){
      return data == USER;
    }

    @Override
    public String getValueRepresentation() {
      if (isTrack()) return "Tracks";
      if (isPlaylist()) return "Playlists";
      if (isUser()) return "Users Playlists";
      return "Not set";
    }

  }
}
