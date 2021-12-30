package com.lopl.melody.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class Logging {

  public static void slashCommand(Class<?> command, SlashCommandEvent event) {
    if (event.getGuild() == null) return;
    if (event.getMember() == null) return;
    LoggerFactory.getLogger(command).info("[" + event.getGuild().getName() + "][" + event.getMember().getEffectiveName() + "]: Triggered " + command.getSimpleName() + ": " + event.getCommandString());
  }

  public static void button(Class<?> command, ButtonClickEvent event) {
    if (event.getGuild() == null) return;
    if (event.getMember() == null) return;
    if (event.getButton() == null) return;
    LoggerFactory.getLogger(command).info("[" + event.getGuild().getName() + "][" + event.getMember().getEffectiveName() + "]: Clicked button: '" + event.getButton().getId() + "' in " + command.getSimpleName() + ": " );
  }

  public static void dropdown(Class<?> command, SelectionMenuEvent event) {
    if (event.getGuild() == null) return;
    if (event.getMember() == null) return;
    if (event.getSelectionMenu() == null) return;
    if (event.getInteraction().getSelectedOptions() == null) return;
    LoggerFactory.getLogger(command).info("[" + event.getGuild().getName() + "][" + event.getMember().getEffectiveName() + "]: Selected: '" + event.getInteraction().getSelectedOptions().get(0).getValue() + "' of '" + event.getSelectionMenu().getId() + "' in " + command.getSimpleName() + ": " );
  }

  public static void info(Class<?> command, @Nullable Guild guild, @Nullable Member member, String message) {
    if (guild == null){
      LoggerFactory.getLogger(command).info(message);
      return;
    }
    if (member == null){
      LoggerFactory.getLogger(command).info("[" + guild.getName() + "]: " + message);
      return;
    }
    LoggerFactory.getLogger(command).info("[" + guild.getName() + "][" + member.getEffectiveName() + "]: " + message);
  }

  public static void debug(Class<?> command, @Nullable Guild guild, @Nullable Member member, String message) {
    if (guild == null){
      LoggerFactory.getLogger(command).debug(message);
      return;
    }
    if (member == null){
      LoggerFactory.getLogger(command).debug("[" + guild.getName() + "]: " + message);
      return;
    }
    LoggerFactory.getLogger(command).debug("[" + guild.getName() + "][" + member.getEffectiveName() + "]: " + message);
  }
}
