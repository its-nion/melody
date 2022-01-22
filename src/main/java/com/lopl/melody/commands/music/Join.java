package com.lopl.melody.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.lopl.melody.commands.record.Record;
import com.lopl.melody.settings.GuildSettings;
import com.lopl.melody.settings.SettingsManager;
import com.lopl.melody.settings.items.AutomaticRecording;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedColor;
import com.lopl.melody.utils.embed.EmbedError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Join Command
 * General functionality is defined here
 * When entering a join command in a guild chat, the bot will try to join your current voice channel
 */
public class Join extends SlashCommand {

  /**
   * Constructor for a Join Command Object. This is instantiated when building the Command Handler or for temporary operations
   * <p>
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
   *
   * @param event all the event data
   */
  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    if (event.getMember() == null || event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
      event.replyEmbeds(EmbedError.with("This Command requires **you** to be **connected to a voice channel**")).queue();
      return;
    }

    if (event.getGuild() == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    AudioManager audioManager = event.getGuild().getAudioManager();
    VoiceChannel memberChannel = event.getMember().getVoiceState().getChannel();
    GuildVoiceState botVS = event.getGuild().getSelfMember().getVoiceState();
    VoiceChannel old = botVS == null ? null : botVS.getChannel();

    List<MessageEmbed> embeds = new ArrayList<>();
    MessageEmbed joinEb = createJoinMessageEmbed(old, memberChannel);
    embeds.add(joinEb);
    audioManager.openAudioConnection(memberChannel);

    GuildSettings guildSettings = SettingsManager.getInstance().getGuildSettings(event.getGuild());
    AutomaticRecording.Value autoRecord = guildSettings.getSetting(AutomaticRecording.class).getValue();
    if (autoRecord.isWithMessage()) {
      MessageEmbed recEb = new Record().getRecordMessage(memberChannel);
      if (recEb != null) embeds.add(recEb);
    }
    if (autoRecord.isOn()) {
      new Record().record(memberChannel);
    }

    event.replyEmbeds(embeds).queue();
    Logging.info(getClass(), event.getGuild(), null, "Joined channel " + memberChannel.getName());
  }

  /**
   * This function can be call from other Slash Functions. It will connect the bot to your voicechannel and return
   * a fitting MessageEmbed.
   *
   * @param event the calling SlashCommandEvent
   * @return a fitting MessageEmbed
   */
  public List<MessageEmbed> connectReturnEmbed(SlashCommandEvent event) {
    if (event.getGuild() == null) {
      return new ArrayList<>(Collections.singletonList(EmbedError.with("This command can only be executed in a server textchannel")));
    }

    if (event.getMember() == null || event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
      return new ArrayList<>(Collections.singletonList(EmbedError.with("This Command requires **you** to be **connected to a voice channel**")));
    }

    VoiceChannel channel = event.getMember().getVoiceState().getChannel();
    GuildVoiceState botVS = event.getGuild().getSelfMember().getVoiceState();
    VoiceChannel old = botVS == null ? null : botVS.getChannel();

    MessageEmbed joinMessageEmbed = getJoinMessageEmbed(old, channel);
    AudioManager audio = event.getGuild().getAudioManager();
    audio.openAudioConnection(channel);
    if (joinMessageEmbed == null) return new ArrayList<>();

    GuildSettings guildSettings = SettingsManager.getInstance().getGuildSettings(event.getGuild());
    AutomaticRecording.Value autoRecord = guildSettings.getSetting(AutomaticRecording.class).getValue();
    if (autoRecord.isWithMessage()) {
      MessageEmbed recEb = new Record().getRecordMessage(channel);
      if (recEb != null) return new ArrayList<>(List.of(joinMessageEmbed, recEb));
    }
    if (autoRecord.isOn()) {
      new Record().record(channel);
    }

    return new ArrayList<>(Collections.singletonList(joinMessageEmbed));
  }

  /**
   * This function can be called from anywhere.
   * It will connect the bot to a voicechannel and send a fitting message to the provided TextChannel
   *
   * @param guild       the current guild
   * @param member      the requester
   * @param textChannel the TextChannel for a Message. Can be null
   */
  public void connect(@Nonnull Guild guild, @Nullable Member member, @Nullable TextChannel textChannel) {
    AudioManager audio = guild.getAudioManager();

    if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
      if (textChannel != null)
        textChannel.sendMessageEmbeds(EmbedError.with("This Command requires **you** to be **connected to a voice channel**")).queue();
      return;
    }

    GuildVoiceState botVS = guild.getSelfMember().getVoiceState();
    VoiceChannel old = botVS == null ? null : botVS.getChannel();
    VoiceChannel channel = member.getVoiceState().getChannel();
    MessageEmbed joinMessageEmbed = getJoinMessageEmbed(old, channel);
    if (textChannel != null && joinMessageEmbed != null)
      textChannel.sendMessageEmbeds(joinMessageEmbed).queue();
    audio.openAudioConnection(channel);
  }

  /**
   * Returns a MessageEmbed with a descriptive content, how the bot behaved, while joining your VoiceChannel.
   *
   * @param newChannel The joining Channel
   * @param oldChannel The previous channel, null if not connected
   * @return a fitting MessageEmbed
   */
  @Nullable
  private MessageEmbed getJoinMessageEmbed(@Nullable VoiceChannel oldChannel, @Nonnull VoiceChannel newChannel) {
    if (oldChannel == null) {
      return new EmbedBuilder()
          .setColor(EmbedColor.GREEN)
          .setDescription("**Joined** in <#" + newChannel.getId() + ">")
          .build();
    }
    if (oldChannel.getIdLong() != newChannel.getIdLong())
      return new EmbedBuilder()
          .setColor(EmbedColor.BLUE)
          .setDescription("**Moved** to <#" + newChannel.getId() + ">")
          .build();
    return null;
  }

  /**
   * Returns a MessageEmbed with a descriptive content, how the bot behaved, while joining your VoiceChannel.
   * Also returns a embed when nothing happened
   *
   * @param newChannel The joining Channel
   * @param oldChannel The previous channel, null if not connected
   * @return a fitting MessageEmbed
   */
  @Nonnull
  private MessageEmbed createJoinMessageEmbed(@Nullable VoiceChannel oldChannel, @Nonnull VoiceChannel newChannel) {
    if (oldChannel == null) {
      return new EmbedBuilder()
          .setColor(EmbedColor.GREEN)
          .setDescription("**Joined** in <#" + newChannel.getId() + ">")
          .build();
    }
    if (oldChannel.getIdLong() != newChannel.getIdLong())
      return new EmbedBuilder()
          .setColor(EmbedColor.BLUE)
          .setDescription("**Moved** to <#" + newChannel.getId() + ">")
          .build();
    return new EmbedBuilder()
        .setColor(EmbedColor.RED)
        .setDescription("**I'm already connected** to <#" + newChannel.getId() + ">")
        .build();
  }
}
