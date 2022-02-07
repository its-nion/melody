package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.util.HashMap;

public class DropdownManager {
  //public static final int MAX_CACHE = 50;

  private final HashMap<String, SlashCommand> dropdownCache;

  public DropdownManager() {
    this.dropdownCache = new HashMap<>();
  }

  public void cache(SelectionMenu dropdown, SlashCommand slashCommand) {
    dropdownCache.put(dropdown.getId(), slashCommand);
  }

  public SlashCommand request(SelectionMenu dropdown) {
    return dropdownCache.getOrDefault(dropdown.getId(), null);
  }
}
