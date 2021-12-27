package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.handler.PlayerManager;
import audioCore.slash.SlashCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import utils.annotation.NoUserCommand;

import java.awt.*;

@NoUserCommand
public class Resume extends SlashCommand {


    @Override
    protected void execute(SlashCommandEvent event) {
        if (!AudioStateChecks.isMemberInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248, 78, 106, 255))
                .setDescription("This Command requires **you** to be **connected to a voice channel**")
                .build())
                .queue();

            return;
        }

        if(!AudioStateChecks.isMelodyInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("This Command requires Melody to be **connected to a voice channel**")
                .build())
                .queue();

            return;
        }

        if (AudioStateChecks.isMelodyInVC(event)) {
            if (!AudioStateChecks.isMemberAndMelodyInSameVC(event)) {
                event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(248, 78, 106, 255))
                    .setDescription("This Command requires **you** to be **in the same voice channel as Melody**")
                    .build())
                    .queue();

                return;
            }
        }

        if (!PlayerManager.getInstance().getGuildAudioManager(event.getGuild()).player.isPaused())
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248, 78, 106, 255))
                .setDescription("The player is **not paused**")
                .build())
                .queue();

            return;
        }

        PlayerManager.getInstance().getGuildAudioManager(event.getGuild()).player.setPaused(false);

        event.replyEmbeds(new EmbedBuilder()
            .setDescription("**Resumed** the player")
            .build())
            .queue();

        return;
    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }

    public void resume(Guild guild){
        PlayerManager manager = PlayerManager.getInstance();
        AudioPlayer player = manager.getGuildAudioManager(guild).player;
        if (!player.isPaused()) return;
        player.setPaused(false);
    }
}
