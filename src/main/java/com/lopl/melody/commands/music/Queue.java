package com.lopl.melody.commands.music;

import com.lopl.melody.audioCore.util.AudioStateChecks;
import com.lopl.melody.audioCore.handler.GuildAudioManager;
import com.lopl.melody.audioCore.handler.PlayerManager;
import com.lopl.melody.audioCore.handler.TrackScheduler;
import com.lopl.melody.audioCore.slash.SlashCommand;
import com.jagrosh.jdautilities.command.Command.Category;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedColor;
import com.lopl.melody.utils.embed.EmbedError;
import com.lopl.melody.utils.embed.ReactionEmoji;

import java.util.ArrayList;
import java.util.List;

public class Queue extends SlashCommand {

  public static final String Shuffle = "shuffle";
  public static final String QueueSkipForward = "q_skip";
  public static final String QueueSkipBackwards = "q_skip_reverse";
  public static final String DeleteQueue = "delete_queue";

  public Queue() {
    super.name = "queue";
    super.category = new Category("Sound");
    super.help = "/queue : shows the current queue"; // TODO set good help and description
    super.description = "shows the current queue";
  }

  @Nullable
  @Override
  public List<String> allowAnonymousComponentCall() {
    return List.of(Shuffle, QueueSkipForward, QueueSkipBackwards, DeleteQueue);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    PlayerManager manager = PlayerManager.getInstance();
    Guild guild = event.getGuild();

    if (guild == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
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

    GuildAudioManager guildAudioManager = manager.getGuildAudioManager(guild);

    if (guildAudioManager.scheduler.getQueue().isEmpty()) {
      event.replyEmbeds(getQueueEmbed(guildAudioManager)).queue();
      return;
    }

    MessageEmbed embed = getQueueEmbed(guildAudioManager);
    ActionRow buttons = getActionRow(guildAudioManager);
    event.replyEmbeds(embed).addActionRows(buttons).queue();
    for (Button button : buttons.getButtons())
      registerButton(button);
  }

  @NotNull
  private ActionRow getActionRow(GuildAudioManager guildAudioManager){
    ArrayList<AudioTrack> queue = guildAudioManager.scheduler.getQueue();

    Button bPrevious = Button.danger(QueueSkipBackwards, Emoji.fromMarkdown(ReactionEmoji.BACKWARDS)).asDisabled();
    Button bSkip = Button.secondary(QueueSkipForward, Emoji.fromMarkdown(ReactionEmoji.SKIP)).withDisabled(queue.isEmpty());
    Button bShuffle = Button.secondary(Shuffle, Emoji.fromMarkdown(ReactionEmoji.SHUFFLE)).withDisabled(queue.isEmpty());
    Button bStop = Button.secondary(DeleteQueue, Emoji.fromMarkdown(ReactionEmoji.STOP)).withDisabled(queue.isEmpty());

    return ActionRow.of(bPrevious, bSkip, bShuffle, bStop);
  }

  @NotNull
  private MessageEmbed getQueueEmbed(GuildAudioManager guildAudioManager) {
    TrackScheduler scheduler = guildAudioManager.scheduler;
    AudioPlayer player = guildAudioManager.player;
    AudioTrackInfo trackInfo = player.getPlayingTrack().getInfo();
    if (scheduler.getQueue().isEmpty())
      return EmbedError.with("There is **no queue**. Use /play to add songs");
    return new EmbedBuilder()
        .setAuthor(trackInfo.author + "\n" + trackInfo.title)
        .setThumbnail(getThumbnail(trackInfo.uri))
        .setTitle(scheduler.getQueue().size() + " Tracks queued:")
        //.setThumbnail("https://raw.githubusercontent.com/Phil0L/DonutMusic/master/imgs/Donut-Bot-Queue.png")
        .setColor(EmbedColor.BLUE)
        .setDescription(getQueue(guildAudioManager)).build();
  }

  private String getThumbnail(String uri){
    // i.e. https://www.youtube.com/watch?v=dQw4w9WgXcQ
    uri = uri.replaceAll("//youtube", "//img.youtube");
    uri = uri.replaceAll("www", "img");
    uri = uri.replaceAll("watch\\?v=", "vi/");
    uri = uri + "/hqdefault.jpg";
    // i.e. https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg
    return uri;
  }

  @NotNull
  private String getQueue(GuildAudioManager guildAudioManager) {
    final int LIST_SIZE = 10;

    StringBuilder desc = new StringBuilder();
    AudioTrack[] tracks = guildAudioManager.scheduler.getQueue().toArray(AudioTrack[]::new);
    for (int i = 0; i < LIST_SIZE -1; i++) {
      if (i >= tracks.length) break;
      AudioTrack track = tracks[i];
      desc.append(ReactionEmoji.getNumberAsEmoji(i + 1)).append(" ").append(track.getInfo().title).append("\n");
    }
    if (tracks.length == LIST_SIZE) {
      AudioTrack track = tracks[LIST_SIZE - 1];
      desc.append(ReactionEmoji.getNumberAsEmoji(LIST_SIZE)).append(" ").append(track.getInfo().title).append("\n");
    }
    if (tracks.length >= LIST_SIZE + 1) {
      int i = tracks.length - 1;
      AudioTrack track = tracks[i];
      desc.append("... ").append(tracks.length - LIST_SIZE + 1).append(" more").append(" ...").append("\n");
      desc.append(ReactionEmoji.getNumberAsEmoji(i + 1)).append(" ").append(track.getInfo().title).append("\n");
    }
    return desc.toString();
  }

  @Override
  protected void clicked(ButtonClickEvent event, boolean anonymous) {
    Logging.button(getClass(), event);

    if (event.getGuild() == null){
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    if (!AudioStateChecks.isMelodyInVC(event)){
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

    if (event.getButton() == null || event.getButton().getId() == null || event.getButton().getEmoji() == null){
      event.replyEmbeds(EmbedError.with("Something is wrong with this button...")).queue();
      return;
    }

    PlayerManager manager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = manager.getGuildAudioManager(event.getGuild());
    TrackScheduler scheduler = guildAudioManager.scheduler;

    switch (event.getButton().getId()){
      case Shuffle -> scheduler.shuffle();
      case QueueSkipForward -> new Skip().skip(event.getGuild());
      case QueueSkipBackwards -> {} // TODO Not implemented
      case DeleteQueue -> scheduler.clearQueue();
      default -> {return;}
    }

    // reload Message
    Message message = event.getMessage();
    MessageEmbed embed = getQueueEmbed(guildAudioManager);
    ActionRow buttons = getActionRow(guildAudioManager);

    message.editMessageEmbeds(embed).queue();
    event.editComponents(buttons).queue();
    for (Button button : buttons.getButtons())
      registerButton(button);
  }
}
