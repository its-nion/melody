package utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class Logging {

  public static void slashCommand(Class<?> command, SlashCommandEvent event) {
    if (event.getGuild() == null) return;
    if (event.getMember() == null) return;
    LoggerFactory.getLogger(command).info("[" + event.getGuild().getName() + "][" + event.getMember().getEffectiveName() + "]: Triggered " + command.getSimpleName() + ": " + event.getCommandString());
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
