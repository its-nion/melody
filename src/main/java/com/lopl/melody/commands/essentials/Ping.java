package com.lopl.melody.commands.essentials;

import com.jagrosh.jdautilities.command.Command.Category;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class Ping extends SlashCommand {
  public Ping() {
    super.name = "ping";
    super.category = new Category("Essentials");
    super.description = "Shows the bots ping";
    super.help = "/ping : shows the bots ping";
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    long ping = event.getJDA().getGatewayPing();
    event.replyEmbeds(new EmbedBuilder()
        .setColor(getColorByPing(ping))
        .setTitle(ping + " ms")
        .build())
        .setEphemeral(true)
        .queue();
    Logging.debug(getClass(), event.getGuild(), null, "Current ping is " + ping + "ms");
  }

  private Color getColorByPing(long ping) {
    if (ping < 100) return Color.cyan;
    if (ping < 400) return Color.green;
    if (ping < 700) return Color.yellow;
    if (ping < 1000) return Color.orange;
    return Color.red;
  }
}
