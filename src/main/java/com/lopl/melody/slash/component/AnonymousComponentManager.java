package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AnonymousComponentManager {
  //public static final int MAX_CACHE = 50;

  private final HashMap<String, SlashCommand> idCache;

  public AnonymousComponentManager(List<SlashCommand> commands) {
    this.idCache = new HashMap<>();
    this.cache(commands);
    List<String> loggedIds = idCache.keySet().stream().filter(c -> !c.contains("volume")).collect(Collectors.toList());
    if (idCache.containsKey("volume")) loggedIds.add("volume_1-100");
    Logging.debug(getClass(), null, null, "Cached anonymous IDs:" + Arrays.toString(loggedIds.toArray()));
  }

  public void cache(List<SlashCommand> commands) {
    for (SlashCommand command : commands) {
      List<String> ids = command.allowAnonymousComponentCall();
      if (ids == null || ids.isEmpty()) continue;
      for (String id : ids) {
        cache(id, command);
      }
    }
  }

  public void cache(String id, SlashCommand slashCommand) {
    if (idCache.containsKey(id))
      throw new RuntimeException("No duplicate Component ID allowed: " + id);
    idCache.put(id, slashCommand);
  }

  public SlashCommand request(String component) {
    return idCache.getOrDefault(component, null);
  }

  public boolean contains(String id){
    return idCache.containsKey(id);
  }
}
