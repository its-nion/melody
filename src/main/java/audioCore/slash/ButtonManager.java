package audioCore.slash;

import net.dv8tion.jda.api.interactions.components.Button;

import java.util.HashMap;

public class ButtonManager {
  //public static final int MAX_CACHE = 50;

  private final HashMap<String, SlashCommand> buttonCache;

  public ButtonManager() {
    this.buttonCache = new HashMap<>();
  }

  public void cache(Button button, SlashCommand slashCommand){
    buttonCache.put(button.getId(), slashCommand);
  }

  public SlashCommand request(Button button){
    return buttonCache.getOrDefault(button.getId(), null);
  }
}
