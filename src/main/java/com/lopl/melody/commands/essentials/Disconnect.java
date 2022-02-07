package com.lopl.melody.commands.essentials;

import com.jagrosh.jdautilities.command.Command;
import com.lopl.melody.audio.util.AudioStateChecks;
import com.lopl.melody.commands.music.Stop;
import com.lopl.melody.commands.record.Record;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedError;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Disconnect extends SlashCommand {

  public Disconnect() {
    super.name = "disconnect";
    super.category = new Command.Category("Essentials");
    super.help = "/disconnect : disconnects the bot from his channel";
    super.description = "disconnects the bot from his channel";
  }

  @Nullable
  @Override
  public ListenerAdapter getCommandEventListener() {
    return new AutomaticDisconnect();
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
    new Record().stop(guild);
    disconnect(guild);
    VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();
    event.replyEmbeds(EmbedError.friendly("**Disconnected** from <#" + memberChannel.getId() + ">")).queue();
  }

  public void disconnect(Guild guild) {
    if (guild == null) return;
    AudioManager audioManager = guild.getAudioManager();
    audioManager.closeAudioConnection();
    Logging.info(getClass(), guild, null, "Disconnected");
  }


  public static class AutomaticDisconnect extends ListenerAdapter {

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
      if (!event.getGuild().getAudioManager().isConnected()) return; // not even connected
      VoiceChannel botChannel = event.getGuild().getAudioManager().getConnectedChannel();
      if (botChannel == null) return; // also not connected to a guild
      VoiceChannel eventChannel = event.getChannelLeft();
      if (botChannel.getIdLong() != eventChannel.getIdLong()) return; // not in the same guild channel
      if (eventChannel.getMembers().stream().filter(m -> !m.getUser().isBot()).toArray().length != 0)
        return; // I am not the last in this channel (bots don't count)
      if (eventChannel.getMembers().get(0).getUser().getIdLong() != event.getJDA().getSelfUser().getIdLong()) return;
      new Disconnect().disconnect(event.getGuild());
    }
  }
}
