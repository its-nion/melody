package com.lopl.melody.slash;


import com.lopl.melody.commands.essentials.*;
import com.lopl.melody.commands.music.*;
import com.lopl.melody.commands.record.Clip;
import com.lopl.melody.commands.record.Record;

import java.util.List;

public class SlashCommands {
  private static final List<SlashCommand> commands = List.of(
      new Ping(),
      new Info(),
      new Help(),
      new Settings(),

      new Play(),
      new Player(),
      new Join(),
      new Disconnect(),
      new Pause(),
      new Resume(),
      new Stop(),
      new Skip(),
      new Volume(),
      new Queue(),
      new Mixer(),

      new Record(),
      new Clip()
  );

  public static List<SlashCommand> getCommands() {
    return commands;
  }

}
