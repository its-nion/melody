package com.lopl.melody.utils.message;

import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is able to keep track of instances of {@link SavedMessage} and all classes that extend it.
 * Use {@link #saveMessage(SavedMessage)} to cache a message.
 * Use {@link #getMessage(Message)} to retrieve the saved Message from a default message.
 */
public class MessageStore {
  private static final List<SavedMessage> savedMessages = new ArrayList<>();

  /**
   * Add a message to the cache.
   * @param message message to cache
   */
  public static void saveMessage(SavedMessage message) {
    savedMessages.add(message);
  }

  /**
   * Get a cached message
   * @param message a default message
   * @return the cached message object or null if not found
   */
  public static SavedMessage getMessage(Message message) {
    for (SavedMessage savedMessage : savedMessages) {
      if (savedMessage.equals(message)) return savedMessage;
    }
    Logging.debug(MessageStore.class, message.getGuild(), null, "Found no message with id: " + message.getId());
    return null;
  }

  /**
   * Getter for all cached messages
   * @return a list
   */
  public static List<SavedMessage> allMessages() {
    return savedMessages;
  }
}
