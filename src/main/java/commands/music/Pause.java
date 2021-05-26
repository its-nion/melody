package commands.music;

import audioCore.AudioStateChecks;
import audioCore.GuildMusicManager;
import audioCore.PlayerManager;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public class Pause implements Command
{

    @Override
    public CommandData commandInfo() {
        return new CommandData("pause", "Pauses the player");
    }

    @Override
    public void called(SlashCommandEvent event) {
        if (!AudioStateChecks.isMemberInVC(event))
        {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248, 78, 106, 255))
                    .setDescription("This Command requires **you** to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        if(AudioStateChecks.isMelodyInVC(event) == false)
        {
            event.reply(new EmbedBuilder()
                    .setColor(new Color(248,78,106,255))
                    .setDescription("This Command requires Melody to be **connected to a voice channel**")
                    .build())
                    .queue();

            return;
        }

        if (AudioStateChecks.isMelodyInVC(event)) {
            if (AudioStateChecks.isMemberAndMelodyInSameVC(event) == false) {
                event.reply(new EmbedBuilder()
                        .setColor(new Color(248, 78, 106, 255))
                        .setDescription("This Command requires **you** to be **in the same voice channel as Melody**")
                        .build())
                        .queue();

                return;
            }
        }

        if (PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.isPaused())
        {
                event.reply(new EmbedBuilder()
                        .setColor(new Color(248, 78, 106, 255))
                        .setDescription("The player is **already paused**")
                        .build())
                        .queue();

                return;
        }

        this.action(event);
    }

    @Override
    public void action(SlashCommandEvent event) {
        PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.setPaused(true);

        event.reply(new EmbedBuilder()
                .setDescription("**Paused** the player")
                .build())
                .queue();

        return;
    }
}
