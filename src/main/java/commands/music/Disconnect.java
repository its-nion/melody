package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.handler.GuildAudioManager;
import audioCore.handler.PlayerManager;
import audioCore.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class Disconnect extends SlashCommand {

    @Override
    protected void execute(SlashCommandEvent event) {
        if(!AudioStateChecks.isMelodyInVC(event))
        {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(new Color(248,78,106,255))
                .setDescription("This Command requires Melody to be **connected to a voice channel**")
                .build())
                .queue();
            return;
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final GuildAudioManager musicManager = PlayerManager.getInstance().getGuildAudioManager(event.getGuild());

        musicManager.player.stopTrack();
        musicManager.scheduler.clearQueue();

        audioManager.closeAudioConnection();

        event.replyEmbeds(new EmbedBuilder()
            .setColor(new Color(248,78,106,255))
            .setDescription("**Disconnected** from <#" + event.getGuild().getSelfMember().getVoiceState().getChannel().getId() + ">")
            .build())
            .queue();
    }

    @Override
    protected void clicked(ButtonClickEvent event) {

    }
}
