package commands.music;

import audioCore.AudioStateChecks;
import audioCore.GuildMusicManager;
import audioCore.PlayerManager;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class Disconnect implements Command
{
    @Override
    public CommandData commandInfo()
    {
        return new CommandData("disconnect", "Resets the player and disconnects Melody from your voice channel");
    }

    @Override
    public void called(SlashCommandEvent event)
    {
        if(AudioStateChecks.isMelodyInVC(event) == false)
        {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires Melody to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        this.action(event);
    }

    @Override
    public void action(SlashCommandEvent event)
    {
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();

        audioManager.closeAudioConnection();

        event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("**Disconnected** from <#" + event.getGuild().getSelfMember().getVoiceState().getChannel().getId() + ">")
                .build())
                .queue();
    }
}
