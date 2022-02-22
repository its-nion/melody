package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.util.HashMap;

/**
 * This class adds the functionality to execute a dropdown action on a registered SelectionMenu.
 * The dropdown has to be registered at the SlashCommand before.
 */
public class DropdownManager {
  //public static final int MAX_CACHE = 50;

  private final HashMap<String, SlashCommand> dropdownCache;

  public DropdownManager() {
    this.dropdownCache = new HashMap<>();
  }

  /**
   * This will cache a dropdown to a slashCommand to the dropdownCache
   * @param dropdown a selectionMenu
   * @param slashCommand a according SlashCommand
   */
  public void cache(SelectionMenu dropdown, SlashCommand slashCommand) {
    if (dropdown == null || slashCommand == null) return;
    dropdownCache.put(dropdown.getId(), slashCommand);
  }

  /**
   * Gets a SlashCommand from the dropdownCache for a given selectionMenu
   * @param dropdown a registered dropdown
   * @return the mapped SlashCommand or null if not mapped
   */
  public SlashCommand request(SelectionMenu dropdown) {
    if (dropdown == null) return null;
    return dropdownCache.getOrDefault(dropdown.getId(), null);
  }
}
