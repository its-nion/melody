package commands.music;

import audioCore.AudioStateChecks;
import audioCore.GuildMusicManager;
import audioCore.PlayerManager;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public class Stop implements Command
{

    @Override
    public CommandData commandInfo() {
        return new CommandData("stop", "Stops the player and clears the queue");
    }

    @Override
    public void called(SlashCommandEvent event) {
        if(!AudioStateChecks.isMemberInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires **you** to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        if(!AudioStateChecks.isMelodyInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires Melody to be **connected to your voice channel**")
                    .build())
                    .queue();

            return;
        }

        if(!AudioStateChecks.isMemberAndMelodyInSameVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires Melody to be **connected to your voice channel**")
                    .build())
                    .queue();

            return;
        }

        action(event);
    }

    @Override
    public void action(SlashCommandEvent event)
    {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Player stopped and queue cleared")
                .build())
                .queue();
    }
}
