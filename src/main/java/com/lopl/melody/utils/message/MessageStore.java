package com.lopl.melody.utils.message;

import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageStore {
  private static final List<SavedMessage> savedMessages = new ArrayList<>();

  public static void saveMessage(SavedMessage message) {
    savedMessages.add(message);
  }

  public static SavedMessage getMessage(Message message) {
    for (SavedMessage savedMessage : savedMessages) {
      if (savedMessage.equals(message)) return savedMessage;
    }
    Logging.debug(MessageStore.class, message.getGuild(), null, "Found no message with id: " + message.getId());
    return null;
  }

  public static List<SavedMessage> allMessages() {
    return savedMessages;
  }
}
