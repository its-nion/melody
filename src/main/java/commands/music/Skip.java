package commands.music;

import audioCore.AudioStateChecks;
import audioCore.GuildMusicManager;
import audioCore.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class Skip implements Command
{

    @Override
    public CommandData commandInfo() {
        return new CommandData("skip", "Skips the current track")
                .addOption(new OptionData(INTEGER, "amount", "How many songs to skip")
                        .setRequired(false));
    }

    @Override
    public void called(SlashCommandEvent event) {
        if(!AudioStateChecks.isMemberInVC(event))
        {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires **you** to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        if(!AudioStateChecks.isMelodyInVC(event))
        {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires Melody to be **connected to your voice channel**")
                    .build())
                    .queue();

            return;
        }

        if(!AudioStateChecks.isMemberAndMelodyInSameVC(event))
        {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires Melody to be **connected to your voice channel**")
                    .build())
                    .queue();

            return;
        }

        final AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer;

        if(audioPlayer.getPlayingTrack() == null)
        {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("There is currently **no track playing**")
                    .build())
                    .queue();

            return;
        }

        action(event);
    }

    @Override
    public void action(SlashCommandEvent event) {
        SlashCommandEvent.OptionData option = event.getOption("amount");

        if(option == null)
        {
            PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.nextTrack();

            event.reply(new EmbedBuilder()
                    .setDescription("**Skipped** to the next song")
                    .build())
                    .queue();
        }
        else
        {
            PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.skipTracks(event, event.getOption("amount").getAsLong());
        }

        return;
    }
}
