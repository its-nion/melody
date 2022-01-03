package com.lopl.melody.commands.music;

import com.lopl.melody.audio.util.AudioStateChecks;
import com.lopl.melody.audio.handler.GuildAudioManager;
import com.lopl.melody.audio.handler.PlayerManager;
import com.lopl.melody.audio.handler.TrackScheduler;
import com.lopl.melody.slash.SlashCommand;
import com.jagrosh.jdautilities.command.Command.Category;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedColor;
import com.lopl.melody.utils.embed.EmbedError;
import com.lopl.melody.utils.embed.ReactionEmoji;

import java.util.List;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class Skip extends SlashCommand {

  public static final String Forwards = "skip_forward";
  public static final String Backwards = "skip_backward";
  public static final String SKIP_AMOUNT = "skip_amount";

  public Skip() {
    super.name = "skip";
    super.category = new Category("Sound");
    super.help = "/skip : Skips the current track " +
        "/skip 3 : skips the current and 2 more songs";
    super.description = "Skips the current track";
  }

  @Override
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca.addOption(INTEGER, SKIP_AMOUNT, "How many songs to skip", false);
  }

  @Nullable
  @Override
  public List<String> allowAnonymousComponentCall() {
    return List.of(Forwards, Backwards);
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
    AudioPlayer audioPlayer = guildAudioManager.player;


    if (audioPlayer.getPlayingTrack() == null) {
      event.replyEmbeds(EmbedError.with("There is currently **no track playing**")).queue();
      return;
    }

    final OptionMapping option = event.getOption(SKIP_AMOUNT);
    int amount = option == null ? 1 : (int) option.getAsLong();
    skip(guild, amount);
    event.replyEmbeds(getSkipEmbed(audioPlayer.getPlayingTrack(), guildAudioManager))
         .addActionRows(getActionRow(guild))
         .queue();
  }

  private MessageEmbed getSkipEmbed(AudioTrack audioTrack, GuildAudioManager guildAudioManager){
    if (audioTrack == null)
      return new EmbedBuilder().setDescription("No track currently playing.").build();

    AudioTrackInfo trackInfo = audioTrack.getInfo();
    EmbedBuilder eb = new EmbedBuilder()
        .setColor(EmbedColor.BLUE)
        .setTitle(trackInfo.title)
        .setAuthor(trackInfo.author)
        .setThumbnail(getThumbnail(trackInfo.uri))
        .setDescription(getQueue(guildAudioManager));
    return eb.build();
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
    final int LIST_SIZE = 6;

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

  private ActionRow getActionRow(Guild guild){
    PlayerManager manager = PlayerManager.getInstance();
    TrackScheduler scheduler = manager.getGuildAudioManager(guild).scheduler;
    boolean hasNextTrack = !scheduler.getQueue().isEmpty();

    Button bPrevious = Button.danger(Backwards, Emoji.fromMarkdown(ReactionEmoji.BACKWARDS)).asDisabled();
    Button bSkip = Button.secondary(Forwards, Emoji.fromMarkdown(ReactionEmoji.SKIP)).withDisabled(!hasNextTrack);

    return ActionRow.of(bPrevious, bSkip);
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

    switch (event.getButton().getId()){
      case Forwards -> new Skip().skip(event.getGuild());
      case Backwards -> {} // TODO Not implemented
      default -> {return;}
    }

    // reload Message
    PlayerManager manager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = manager.getGuildAudioManager(event.getGuild());
    Message message = event.getMessage();
    MessageEmbed embed = getSkipEmbed(guildAudioManager.player.getPlayingTrack(), guildAudioManager);
    ActionRow buttons = getActionRow(event.getGuild());

    message.editMessageEmbeds(embed).queue();
    event.editComponents(buttons).queue();
    for (Button button : buttons.getButtons())
      registerButton(button);

  }

  public boolean skip(Guild guild) {
    PlayerManager manager = PlayerManager.getInstance();
    TrackScheduler scheduler = manager.getGuildAudioManager(guild).scheduler;
    return scheduler.nextTrack();
  }

  public boolean skip(Guild guild, int amountLeft) {
    if (amountLeft == 0) return true;
    if (!skip(guild)) return false;
    return skip(guild, amountLeft - 1);
  }
}
