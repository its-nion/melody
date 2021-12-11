package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Error {

  public static MessageEmbed with(String message){
    return new EmbedBuilder()
        .setColor(EmbedColor.RED)
        .setDescription(message)
        .build();
  }

}
