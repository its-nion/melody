package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.handler.GuildAudioManager;
import audioCore.handler.PlayerManager;
import audioCore.slash.SlashCommand;
import melody.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Logging;
import utils.embed.EmbedError;

import java.awt.*;

public class Disconnect extends SlashCommand {

    @Override
    protected void execute(SlashCommandEvent event) {
        Logging.slashCommand(getClass(), event);

        Guild guild = event.getGuild();

        if (guild == null) {
            event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
            return;
        }

        if (event.getMember() == null || event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
            event.replyEmbeds(EmbedError.with("This Command requires **you** to be **connected to a voice channel**")).queue();
            return;
        }

        if (!AudioStateChecks.isMelodyInVC(event)) {
            event.replyEmbeds(EmbedError.with("This Command requires **Melody** to be **connected to a voice channel**")).queue();
            return;
        }

        if (!AudioStateChecks.isMemberAndMelodyInSameVC(event)) {
            GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
            if (voiceState == null) return; // should be covered by the check above
            VoiceChannel channel = voiceState.getChannel();
            if (channel == null) return; // should be covered by the check above
            event.replyEmbeds(EmbedError.withFormat("This Command requires **you** to be **connected to Melody's voice channel** <#%S>", channel.getId())).queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        GuildAudioManager musicManager = PlayerManager.getInstance().getGuildAudioManager(event.getGuild());
        VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();
        musicManager.player.stopTrack();
        musicManager.scheduler.clearQueue();
        audioManager.closeAudioConnection();
        event.replyEmbeds(EmbedError.friendly("**Disconnected** from <#" + memberChannel.getId() + ">")).queue();
    }
}
