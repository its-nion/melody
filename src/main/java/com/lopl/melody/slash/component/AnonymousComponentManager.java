package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.slash.SlashCommandClient;
import com.lopl.melody.utils.Logging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class adds the functionality to execute a button or dropdown action without
 * the component being cached. It will recognize the id of the component and will execute the correct method.
 * Ids are stored for every SlashCommand with the {@link SlashCommand#allowAnonymousComponentCall()} method.
 */
public class AnonymousComponentManager {
  //public static final int MAX_CACHE = 50;

  private final HashMap<String, SlashCommand> idCache;

  /**
   * Override constructor with the registered SlashCommandClientCommands.
   */
  public AnonymousComponentManager() {
    this(Arrays.asList(SlashCommandClient.getInstance().slashCommands));
  }

  /**
   * Default constructor with an array of slashCommands.
   * every slashCommands allowAnonymousComponentCall method is collected to the idCache.
   * @param commands a array of commands
   */
  public AnonymousComponentManager(List<SlashCommand> commands) {
    this.idCache = new HashMap<>();
    this.cache(commands);
    List<String> loggedIds = idCache.keySet().stream().filter(c -> !c.contains("volume")).collect(Collectors.toList());
    if (idCache.containsKey("volume")) loggedIds.add("volume_1-100");
    Logging.debug(getClass(), null, null, "Cached anonymous IDs:" + Arrays.toString(loggedIds.toArray()));
  }

  /**
   * This caches all ids of all commands with the overridden method to the idCache
   * @param commands a array of commands
   */
  public void cache(List<SlashCommand> commands) {
    for (SlashCommand command : commands) {
      List<String> ids = command.allowAnonymousComponentCall();
      if (ids == null || ids.isEmpty()) continue;
      for (String id : ids) {
        cache(id, command);
      }
    }
  }

  /**
   * This will cache a id to a slashCommand to the idCache
   * @param id an id as a String
   * @param slashCommand a SlashCommand
   */
  public void cache(String id, SlashCommand slashCommand) {
    if (idCache.containsKey(id))
      throw new RuntimeException("No duplicate Component ID allowed: " + id);
    idCache.put(id, slashCommand);
  }

  /**
   * Gets a SlashCommand from the idCache for a given id
   * @param component a registered id
   * @return the mapped SlashCommand or null if not mapped
   */
  public SlashCommand request(String component) {
    return idCache.getOrDefault(component, null);
  }

  /**
   * @param id a string id
   * @return true if the id is present, else false
   */
  public boolean contains(String id){
    return idCache.containsKey(id);
  }
}
