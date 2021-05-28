package commands.essentials;

import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.button.Button;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Info implements Command
{
    @Override
    public CommandData commandInfo()
    {
        return new CommandData("info", "Shows information about Melody");
    }

    @Override
    public void called(SlashCommandEvent event)
    {
        this.action(event);
    }

    @Override
    public void action(SlashCommandEvent event)
    {
        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Hey there! If you have any buggs to report, feel free to contact me")
                .build())
                .addActionRow(Button.link("https://github.com/its-nion/melody", "Github"),
                        Button.link("https://discord.com/api/oauth2/authorize?client_id=843" +
                                "104592417390612&permissions=2184202240&scope=bot%20applications.commands", "Invite"))
                .setEphemeral(true)
                .queue();
    }
}
