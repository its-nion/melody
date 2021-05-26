package commands.essentials;

import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
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
        event.reply(new EmbedBuilder()
                .setDescription("[Invite Melody to your server!]" +
                "(https://discord.com/api/oauth2/authorize?client_id=843104592417390612&permissions=2184202240&scope=bot%20applications.commands)")
                .build())
                .setEphemeral(true)
                .queue();
    }
}
