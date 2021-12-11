package commands.essentials;

import audioCore.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class Ping extends SlashCommand
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
    protected void execute(SlashCommandEvent event) {
        long ping = event.getJDA().getGatewayPing();

        event.replyEmbeds(new EmbedBuilder()
            .setColor(getColorByPing(ping))
            .setTitle(ping + " ms")
            .build())
            .setEphemeral(true)
            .queue();
    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }
}
