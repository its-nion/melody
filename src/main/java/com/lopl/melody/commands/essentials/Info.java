package com.lopl.melody.commands.essentials;

import com.jagrosh.jdautilities.command.Command.Category;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;


public class Info extends SlashCommand {

  public Info() {
    super.name = "info";
    super.category = new Category("Essentials");
    super.description = "About me and support";
    super.help = "/ping : shows an information about the bot";
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    event.replyEmbeds(new EmbedBuilder()
        .setDescription("Hey there! If you have any bugs to report, feel free to contact me")
        .build())
        .addActionRow(Button.link("https://github.com/its-nion/melody", "Github"),
            Button.link("https://discord.com/api/oauth2/authorize?client_id=843" +
                "104592417390612&permissions=2184202240&scope=bot%20applications.commands", "Invite"))
        .setEphemeral(true)
        .queue();
  }
}
