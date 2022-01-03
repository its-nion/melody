package com.lopl.melody.commands.music;

import com.lopl.melody.audio.util.AudioStateChecks;
import com.lopl.melody.audio.handler.GuildAudioManager;
import com.lopl.melody.audio.handler.PlayerManager;
import com.lopl.melody.slash.SlashCommand;
import com.jagrosh.jdautilities.command.Command;
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
import com.lopl.melody.utils.embed.EmbedError;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class Volume extends SlashCommand {

  public Volume() {
    super.name = "volume";
    super.category = new Command.Category("Sound");
    super.help = "/volume : shows the current volume \n" +
        "/volume [amount] : sets the volume to amount";
    super.description = "shows and sets the current volume";
  }

  @Nullable
  @Override
  public List<String> allowAnonymousComponentCall() {
    return IntStream.range(1, 101).boxed().map(i -> "volume_" + i).collect(Collectors.toList());
  }

  @Override
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca.addOption(INTEGER, "amount", "Volume in percentage [0 - 100]", false);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    Guild guild = event.getGuild();
    if (guild == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    if (!AudioStateChecks.isMemberInVC(event)) {
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
    OptionMapping option = event.getOption("amount");
    if (option != null) {
      int newVolume = (int) option.getAsLong();
      setVolume(guild, newVolume);
    }

    MessageEmbed embed = getVolumeEmbed(guildAudioManager);
    ActionRow buttons = getActionRow(guildAudioManager);
    event.replyEmbeds(embed).addActionRows(buttons).queue();
    for (Button button : buttons.getButtons())
      registerButton(button);
  }

  public void setVolume(Guild guild, int volume) {
    int newVolume = minMax(0, volume, 100);
    PlayerManager playerManager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = playerManager.getGuildAudioManager(guild);
    guildAudioManager.player.setVolume(newVolume);
    Logging.debug(getClass(), guild, null, "Set Volume to " + volume + "%");
  }

  private MessageEmbed getVolumeEmbed(GuildAudioManager guildAudioManager) {
    int volume = guildAudioManager.player.getVolume();
    return new EmbedBuilder().setTitle("Volume: " + volume + "%").build();
  }

  @NotNull
  private ActionRow getActionRow(GuildAudioManager guildAudioManager) {
    final int[] VOLUME_CHANGES = new int[]{-10, -1, +1, +10};

    int volume = guildAudioManager.player.getVolume();
    Set<Integer> volumes = new HashSet<>();
    for (int volumeChange : VOLUME_CHANGES)
      volumes.add(minMax(0, volume + volumeChange, 100));
    List<Integer> sortedVolumes = new ArrayList<>(volumes);
    Collections.sort(sortedVolumes);

    List<Button> buttons = new ArrayList<>();
    for (int newVolume : sortedVolumes)
      if (newVolume != volume)
        buttons.add(Button.secondary("volume_" + newVolume, newVolume + ""));

    return ActionRow.of(buttons);
  }

  private int minMax(int min, int actual, int max) {
    return Math.max(min, Math.min(actual, max));
  }

  @Override
  protected void clicked(ButtonClickEvent event, boolean anonymous) {
    Logging.button(getClass(), event);

    Guild guild = event.getGuild();
    if (guild == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    if (!AudioStateChecks.isMemberInVC(event)) {
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

    if (event.getButton() == null || event.getButton().getId() == null) {
      event.replyEmbeds(EmbedError.with("Something is wrong with this button...")).queue();
      return;
    }

    String label = event.getButton().getLabel();
    int newVolume;
    try {
      newVolume = Integer.parseInt(label);
    } catch (NumberFormatException e) {
      event.replyEmbeds(EmbedError.with("Something is wrong with this button... This should be a number.")).queue();
      return;
    }

    setVolume(guild, newVolume);

    // reload Message
    PlayerManager playerManager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = playerManager.getGuildAudioManager(guild);
    Message message = event.getMessage();
    MessageEmbed embed = getVolumeEmbed(guildAudioManager);
    ActionRow buttons = getActionRow(guildAudioManager);

    message.editMessageEmbeds(embed).queue();
    event.editComponents(buttons).queue();
    for (Button button : buttons.getButtons())
      registerButton(button);


  }
}
