package com.lopl.melody.utils.message;

import net.dv8tion.jda.api.entities.Message;

/**
 * This is a dataclass object that is used to cache messages.
 * Pass a discord {@link Message} in the constructor to create such a saved message.
 * Store the saved message afterwards with the {@link MessageStore}.
 */
public class SavedMessage {
  private final Message message;
  private final long messageID;

  public SavedMessage(Message message) {
    this.message = message;
    this.messageID = message.getIdLong();
  }

  public Message getMessage() {
    return message;
  }

  public long getMessageID() {
    return messageID;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SavedMessage)
      return ((SavedMessage) obj).message == this.message
          || ((SavedMessage) obj).message.getIdLong() == messageID;
    if (obj instanceof Message)
      return obj == this.message
          || ((Message) obj).getIdLong() == messageID;
    return false;
  }
}
