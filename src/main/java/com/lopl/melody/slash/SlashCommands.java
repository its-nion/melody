package com.lopl.melody.slash;


import com.lopl.melody.commands.essentials.*;
import com.lopl.melody.commands.music.*;
import com.lopl.melody.commands.record.Clip;
import com.lopl.melody.commands.record.Record;

import java.util.List;

/**
 * All registered Commands are listed here.
 * A command needs to be in this List to work.
 */
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

  /**
   * Getter for all commands
   * @return a List of all commands
   */
  public static List<SlashCommand> getCommands() {
    return commands;
  }

}
