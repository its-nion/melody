package com.lopl.melody.commands.record;

import com.jagrosh.jdautilities.command.Command.Category;
import com.lopl.melody.audio.handler.AudioReceiveListener;
import com.lopl.melody.audio.util.AudioStateChecks;
import com.lopl.melody.audio.util.BotRightsManager;
import com.lopl.melody.commands.music.Join;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedColor;
import com.lopl.melody.utils.embed.EmbedError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import java.util.ArrayList;
import java.util.List;

public class Record extends SlashCommand {

  public static final String RECORD_ACTION = "record_action";
  public static final String RECORD_STOP = "record_stop";
  public static final String RECORD_START = "record_start";

  public Record() {
    super.name = "record";
    super.category = new Category("Voice");
    super.help = "/record : starts recording the channel the bot is in\n" +
        "/record stop : stops recording the channel the bot is in";
    super.description = "Starts or stops recording the channel the bot is in";
  }

  @Override
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca.addOptions(
        new OptionData(OptionType.STRING, RECORD_ACTION, "stop or start", false)
            .addChoice("start", RECORD_START)
            .addChoice("stop", RECORD_STOP));
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

    VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();
    if (AudioStateChecks.isMelodyInVC(event) && !AudioStateChecks.isMemberAndMelodyInSameVC(event)) {
      event.replyEmbeds(EmbedError.with("**Melody** is already **connected to another voice channel**")).queue();
      return;
    }

    OptionMapping actionOp = event.getOption(RECORD_ACTION);
    AudioReceiveHandler ah = guild.getAudioManager().getReceivingHandler();
    if (ah instanceof AudioReceiveListener && actionOp != null && !actionOp.getAsString().equals(RECORD_STOP)) {
      event.replyEmbeds(EmbedError.with("**Melody** is already **recording**")).queue();
      return;
    }

    if (ah == null && actionOp != null && actionOp.getAsString().equals(RECORD_STOP)) {
      event.replyEmbeds(EmbedError.with("**Melody** was **not recording**")).queue();
      return;
    }

    AudioManager audio = guild.getAudioManager();
    List<MessageEmbed> embeds = null;
    if (!audio.isConnected())
      embeds = new Join().connectReturnEmbed(event);

    if (actionOp == null || actionOp.getAsString().equals(RECORD_START)) {
      if (embeds == null) embeds = new ArrayList<>();
      if (embeds.size() <= 1)
        embeds.add(createRecordMessage(voiceChannel));
      record(event.getMember().getVoiceState().getChannel());
      event.replyEmbeds(embeds).queue();
    } else if (actionOp.getAsString().equals(RECORD_STOP)) {
      stop(event.getGuild());
      event.replyEmbeds(getRecordStopMessage()).queue();
    }

  }

  public MessageEmbed getRecordMessage(VoiceChannel channel) {
    AudioReceiveHandler ah = channel.getGuild().getAudioManager().getReceivingHandler();
    if (ah instanceof AudioReceiveListener) return null;
    return new EmbedBuilder()
        .setColor(EmbedColor.GREEN)
        .setDescription("**Now recording** in <#" + channel.getId() + ">")
        .build();
  }

  public MessageEmbed createRecordMessage(VoiceChannel channel) {
    return new EmbedBuilder()
        .setColor(EmbedColor.GREEN)
        .setDescription("**Now recording** in <#" + channel.getId() + ">")
        .build();
  }

  private MessageEmbed getRecordStopMessage() {
    return new EmbedBuilder()
        .setColor(EmbedColor.RED)
        .setDescription("**Stopped recording**")
        .build();
  }

  public void record(VoiceChannel vc) {
    //initialize the audio receiver listener
    AudioManager audioManager = vc.getGuild().getAudioManager();
    if (audioManager.getReceivingHandler() == null || !(audioManager.getReceivingHandler() instanceof AudioReceiveListener))
      audioManager.setReceivingHandler(new AudioReceiveListener(1, vc));
    BotRightsManager.of(vc.getGuild()).requestUndeafen();
    Logging.info(getClass(), vc.getGuild(), null, "Started recording in " + vc.getName());
  }

  public void stop(Guild guild) {
    AudioReceiveListener ah = (AudioReceiveListener) guild.getAudioManager().getReceivingHandler();
    if (ah != null) {
      ah.canReceive = false;
      ah.compVoiceData = null;
      guild.getAudioManager().setReceivingHandler(null);
    }
    BotRightsManager.of(guild).requestDeafen();
    Logging.debug(getClass(), guild, null, "Destroyed audio handlers for " + guild.getName());
    System.gc();
  }
}
