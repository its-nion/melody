package commands.music;

import audioCore.handler.AudioStateChecks;
import audioCore.handler.GuildAudioManager;
import audioCore.handler.PlayerManager;
import audioCore.slash.SlashCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import utils.Logging;
import utils.annotation.NoUserCommand;
import utils.embed.EmbedError;
import utils.embed.ReactionEmoji;

@NoUserCommand
public class Resume extends SlashCommand {

  public static final String RESUME = "resume";

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

    PlayerManager manager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = manager.getGuildAudioManager(guild);
    AudioPlayer player = guildAudioManager.player;
    boolean paused = player.isPaused();

    if (!paused) {
      event.replyEmbeds(EmbedError.friendly("I am not paused...")).queue();
      return;
    }

    resume(guild);
    Button button = Button.secondary(RESUME, ReactionEmoji.PAUSE);
    event.replyEmbeds(new EmbedBuilder().setDescription("**Resumed** the player").build())
        .addActionRow(button)
        .queue();
    registerButton(button);
  }

  @Deprecated
  @Override
  protected void clicked(ButtonClickEvent event) {
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

    PlayerManager manager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = manager.getGuildAudioManager(guild);
    AudioPlayer player = guildAudioManager.player;
    boolean paused = player.isPaused();
    if (paused) {
      resume(guild);
      event.getMessage().editMessageEmbeds(new EmbedBuilder().setDescription("**Resumed** the player").build()).queue();
    } else {
      new Pause().pause(guild);
      event.getMessage().editMessageEmbeds(new EmbedBuilder().setDescription("**Paused** the player").build()).queue();
    }
    Button button = Button.secondary(RESUME, paused ? ReactionEmoji.PAUSE : ReactionEmoji.RESUME);
    event.editComponents(ActionRow.of(button)).queue();
    registerButton(button);
  }

  public void resume(Guild guild) {
    PlayerManager manager = PlayerManager.getInstance();
    AudioPlayer player = manager.getGuildAudioManager(guild).player;
    if (!player.isPaused()) return;
    player.setPaused(false);
  }
}
