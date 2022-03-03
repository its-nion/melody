package com.lopl.melody.commands.essentials;

import com.jagrosh.jdautilities.command.Command.Category;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
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
  protected void onJDAReady(JDA jda) {
    Logging.info(getClass(), null, null, "My invite link is: " + getInviteLink(jda));
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    event.replyEmbeds(new EmbedBuilder()
        .setDescription("Hey there! If you have any bugs to report, feel free to contact me")
        .build())
        .addActionRow(Button.link("https://github.com/its-nion/melody", "Github"),
            Button.link(getInviteLink(event.getJDA()), "Invite"))
        .setEphemeral(true)
        .queue();

  }

  public String getInviteLink(JDA jda) {
    return jda.getInviteUrl(
        Permission.MESSAGE_READ,
        Permission.VIEW_CHANNEL,
        Permission.MANAGE_CHANNEL,
        Permission.MESSAGE_WRITE,
        Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_ATTACH_FILES,
        Permission.MESSAGE_EMBED_LINKS,
        Permission.MESSAGE_EXT_EMOJI,
        Permission.USE_SLASH_COMMANDS,
        Permission.MESSAGE_HISTORY,
        Permission.VOICE_CONNECT,
        Permission.VOICE_SPEAK,
        Permission.VOICE_MUTE_OTHERS,
        Permission.VOICE_DEAF_OTHERS,
        Permission.VOICE_MOVE_OTHERS,
        Permission.getPermissions(8589934592L).stream().findFirst().orElse(Permission.UNKNOWN) // manage Events
    ).replaceAll("bot", "applications.commands%20bot");
  }
}
