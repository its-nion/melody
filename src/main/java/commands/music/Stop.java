package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.handler.GuildAudioManager;
import audioCore.handler.PlayerManager;
import audioCore.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utils.Logging;
import utils.annotation.NoUserCommand;
import utils.embed.EmbedError;

@NoUserCommand
public class Stop extends SlashCommand {

  @Deprecated
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

    stop(guild);
    new Disconnect().disconnect(guild);
    event.replyEmbeds(new EmbedBuilder().setDescription("**Stopped** the player.\nI will disconnect now").build()).queue();

  }

  public void stop(Guild guild) {
    PlayerManager manager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = manager.getGuildAudioManager(guild);
    guildAudioManager.scheduler.clearQueue();
    guildAudioManager.player.stopTrack();
    guildAudioManager.player.destroy();
  }
}
