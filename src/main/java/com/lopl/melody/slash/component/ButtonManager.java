package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.HashMap;

/**
 * This class adds the functionality to execute a button action on a registered button.
 * The button has to be registered at the SlashCommand before.
 */
public class ButtonManager {
  //public static final int MAX_CACHE = 50;

  private final HashMap<String, SlashCommand> buttonCache;

  public ButtonManager() {
    this.buttonCache = new HashMap<>();
  }

  /**
   * This will cache a button to a slashCommand to the buttonCache
   * @param button a button
   * @param slashCommand a according SlashCommand
   */
  public void cache(Button button, SlashCommand slashCommand) {
    if (button == null || slashCommand == null) return;
    buttonCache.put(button.getId(), slashCommand);
  }

  /**
   * Gets a SlashCommand from the buttonCache for a given button
   * @param button a registered button
   * @return the mapped SlashCommand or null if not mapped
   */
  public SlashCommand request(Button button) {
    if (button == null) return null;
    return buttonCache.getOrDefault(button.getId(), null);
  }
}
