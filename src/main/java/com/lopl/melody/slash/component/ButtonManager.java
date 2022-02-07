package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.HashMap;

public class ButtonManager {
  //public static final int MAX_CACHE = 50;

  private final HashMap<String, SlashCommand> buttonCache;

  public ButtonManager() {
    this.buttonCache = new HashMap<>();
  }

  public void cache(Button button, SlashCommand slashCommand) {
    if (button == null || slashCommand == null) return;
    buttonCache.put(button.getId(), slashCommand);
  }

  public SlashCommand request(Button button) {
    if (button == null) return null;
    return buttonCache.getOrDefault(button.getId(), null);
  }
}
