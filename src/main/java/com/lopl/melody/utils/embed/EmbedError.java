package com.lopl.melody.utils.embed;

import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * This class is used to create Error or Fail Messages easily and uniform.
 */
public class EmbedError {

  /**
   * This will create a Message with a simple String.
   * If you want to use variables in the String consider using {@link #withFormat(String, Object...)}.
   * @param message the displayed String
   * @return a fully built message
   */
  public static MessageEmbed with(String message) {
    Logging.debug(EmbedError.class, null, null, "Encountered user error: " + message);
    return new EmbedBuilder()
        .setColor(EmbedColor.ERROR)
        .setDescription(message)
        .build();
  }

  /**
   * This method is similar to {@link #with(String)}.
   * The only difference is that there is no Logging raised.
   * @param message the displayed String
   * @return a fully built message
   */
  public static MessageEmbed friendly(String message) {
    return new EmbedBuilder()
        .setColor(EmbedColor.ERROR)
        .setDescription(message)
        .build();
  }

  /**
   * This will create a Message with a simple String and formatted variables.
   * @param message the displayed String
   * @param args String format variables. Works like {@link String#format(String, Object...)}.
   *    *        Look there for more information.
   * @return a fully built message
   */
  public static MessageEmbed withFormat(String message, Object... args) {
    Logging.debug(EmbedError.class, null, null, "Encountered user error: " + message);
    return new EmbedBuilder()
        .setColor(EmbedColor.ERROR)
        .setDescription(String.format(message, args))
        .build();
  }

}
