package commands.music;

import audioCore.slash.SlashCommand;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.EmbedColor;
import utils.Error;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Join Command
 * General functionality is defined here
 * When entering a join command in a guild chat, the bot will try to join your current voice channel
 */
public class Join extends SlashCommand {

  /**
   * Constructor for a Join Command Object. This is instantiated when building the Command Handler or for temporary operations
   *
   * {@link #name} The name of the command. The string you have to type after the '/'. i.e. /join
   * {@link #category} The category of the command. Commands will be sorted by that
   * {@link #help} The help message, in case someone types /help [command]
   * {@link #description} This description will be shown together with the command name in the command preview
   */
  public Join() {
    super.name = "join";
    super.category = new Command.Category("Sound");
    super.help = """
        /join : Lets the bot join your current voice channel. This way you wont be alone forever.""";
    super.description = "Lets the bot join your current voice channel";
  }

  /**
   * This will fire whenever a user enters /join in a guild textchannel.
   * If the requesting member is in a vc and the command is correct, the bot will join you.
   * @param event all the event data
   */
  @Override
  protected void execute(SlashCommandEvent event) {
    if (event.getMember() == null || event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
      event.replyEmbeds(Error.with("This Command requires **you** to be **connected to a voice channel**")).queue();
      return;
    }

    if (event.getGuild() == null) {
      event.replyEmbeds(Error.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    AudioManager audioManager = event.getGuild().getAudioManager();
    VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();

    event.replyEmbeds(getJoinMessageEmbed(memberChannel)).queue();
    audioManager.openAudioConnection(memberChannel);
  }

  /**
   * This function can be call from other Slash Functions. It will connect the bot to your voicechannel and return
   * a fitting MessageEmbed.
   * @param event the calling SlashCommandEvent
   * @return a fitting MessageEmbed
   */
  public MessageEmbed connectReturnEmbed(SlashCommandEvent event) {
    if (event.getGuild() == null) {
      return Error.with("This command can only be executed in a server textchannel");
    }

    if (event.getMember() == null || event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
      return Error.with("This Command requires **you** to be **connected to a voice channel**");
    }

    VoiceChannel channel = event.getMember().getVoiceState().getChannel();
    AudioManager audio = event.getGuild().getAudioManager();
    MessageEmbed joinMessageEmbed = getJoinMessageEmbed(channel);
    audio.openAudioConnection(channel);
    return joinMessageEmbed;
  }

  /**
   * This function can be called from anywhere.
   * It will connect the bot to a voicechannel and send a fitting message to the provided TextChannel
   * @param guild the current guild
   * @param member the requester
   * @param textChannel the TextChannel for a Message. Can be null
   */
  public void connect(@Nonnull Guild guild, @Nullable Member member, @Nullable TextChannel textChannel) {
    AudioManager audio = guild.getAudioManager();

    if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
      if (textChannel != null) textChannel.sendMessageEmbeds(Error.with("This Command requires **you** to be **connected to a voice channel**")).queue();
      return;
    }

    VoiceChannel channel = member.getVoiceState().getChannel();
    if (textChannel != null) textChannel.sendMessageEmbeds(getJoinMessageEmbed(channel)).queue();
    audio.openAudioConnection(channel);
  }

  /**
   * Returns a MessageEmbed with a descriptive content, how the bot behaved, while joining your VoiceChannel.
   * @param channel The joining Channel
   * @return a fitting MessageEmbed
   */
  private MessageEmbed getJoinMessageEmbed(VoiceChannel channel) {
    if (channel.getGuild().getSelfMember().getVoiceState() != null)
      return new EmbedBuilder()
          .setColor(EmbedColor.BLUE)
          .setDescription("**Moved** to <#" + channel.getId() + ">")
          .build();
    return new EmbedBuilder()
        .setColor(EmbedColor.GREEN)
        .setDescription("**Joined** in <#" + channel.getId() + ">")
        .build();
  }
}
