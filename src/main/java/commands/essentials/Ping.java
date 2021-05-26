package commands.essentials;

import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nullable;
import java.awt.*;

public class Ping implements Command
{
    private Color getColorByPing(long ping) {
        if (ping < 100)
            return Color.cyan;
        if (ping < 400)
            return Color.green;
        if (ping < 700)
            return Color.yellow;
        if (ping < 1000)
            return Color.orange;
        return Color.red;
    }


    @Override
    public CommandData commandInfo()
    {
        return new CommandData("ping", "Checks Melody's latency");
    }

    @Override
    public void called(SlashCommandEvent event)
    {
        this.action(event);
    }

    @Override
    public void action(SlashCommandEvent event)
    {
        long ping = event.getJDA().getGatewayPing();

        event.reply(new EmbedBuilder()
                .setColor(getColorByPing(ping))
                .setTitle(String.valueOf(ping) + " ms")
                .build())
                .setEphemeral(true)
                .queue();
    }
}
