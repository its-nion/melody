package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.slash.SlashCommand;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Logging;
import utils.embed.EmbedError;

public class Disconnect extends SlashCommand {

    public Disconnect() {
        super.name = "disconnect";
        super.category = new Command.Category("Sound");
        super.help = "/disconnect : disconnects the bot from his channel";
        super.description = "disconnects the bot from his channel";
    }

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

        new Stop().stop(guild);
        disconnect(guild);
        VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();
        event.replyEmbeds(EmbedError.friendly("**Disconnected** from <#" + memberChannel.getId() + ">")).queue();
    }

    public void disconnect(Guild guild){
        if (guild == null) return;
        AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
    }
}
