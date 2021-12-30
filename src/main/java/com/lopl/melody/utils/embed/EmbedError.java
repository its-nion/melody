package com.lopl.melody.utils.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedError {

  public static MessageEmbed with(String message){
    return new EmbedBuilder()
        .setColor(EmbedColor.RED)
        .setDescription(message)
        .build();
  }

  public static MessageEmbed friendly(String message){
    return new EmbedBuilder()
        .setColor(EmbedColor.RED)
        .setDescription(message)
        .build();
  }

  public static MessageEmbed withFormat(String message, Object... args){
    return new EmbedBuilder()
        .setColor(EmbedColor.RED)
        .setDescription(String.format(message, args))
        .build();
  }

}
