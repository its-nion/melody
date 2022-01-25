package com.lopl.melody.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.lopl.melody.audio.handler.GuildAudioManager;
import com.lopl.melody.audio.handler.MixerEqualizer;
import com.lopl.melody.audio.handler.PlayerManager;
import com.lopl.melody.audio.util.AudioStateChecks;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import java.util.List;
import java.util.stream.Collectors;

public class Mixer extends SlashCommand {

  public static final String DROPDOWN_LOW = "dropdown_low";
  public static final String DROPDOWN_MID = "dropdown_mid";
  public static final String DROPDOWN_HIGH = "dropdown_high";
  public static final String MIXER_RESET = "mixer_reset";

  public Mixer() {
    super.name = "mixer";
    super.category = new Command.Category("Sound");
    super.help = """
        /mixer : TODO""";
    super.description = "TODO";
  }

  @Override
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca.addOptions(new OptionData(OptionType.STRING, MIXER_RESET, "leave empty for the mixer", false).addChoice("reset", "reset"));
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    if (event.getGuild() == null) {
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

    PlayerManager playerManager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = playerManager.getGuildAudioManager(event.getGuild());
    MixerEqualizer mixer = guildAudioManager.getMixer();
    if (event.getOption(MIXER_RESET) != null) {
      reset(mixer);
      mixer.save(event.getGuild());
      Logging.info(getClass(), event.getGuild(), null, "Set mixer to " + mixer.toString());
      Logging.info(getClass(), event.getGuild(), null, "Equalizer is now: " + mixer.getGains());
    }
    event.replyEmbeds(getMixerEmbed(mixer)).addActionRows(getMixerDropdowns(mixer)).queue();
  }

  @Override
  protected void dropdown(SelectionMenuEvent event, boolean anonymous) {
    Logging.dropdown(getClass(), event);

    if (event.getGuild() == null) return;
    if (event.getMember() == null) return;
    if (event.getSelectionMenu() == null) return;
    if (event.getInteraction().getSelectedOptions() == null) return;

    PlayerManager playerManager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = playerManager.getGuildAudioManager(event.getGuild());
    MixerEqualizer mixer = guildAudioManager.getMixer();

    String selectedS = event.getInteraction().getSelectedOptions().get(0).getValue();
    String band = selectedS.split("_")[0];
    String valueS = selectedS.split("_")[1];
    int value = Integer.parseInt(valueS);
    switch (band){
      case "low" -> mixer.setLows(value);
      case "mid" -> mixer.setMids(value);
      case "high" -> mixer.setHighs(value);
      default -> {}
    }
    mixer.save(event.getGuild());
    Logging.info(getClass(), event.getGuild(), null, "Set mixer to " + mixer.toString());
    Logging.info(getClass(), event.getGuild(), null, "Equalizer is now: " + mixer.getGains());
    event.getMessage().editMessageEmbeds(getMixerEmbed(mixer)).queue();
    event.editComponents(getMixerDropdowns(mixer)).queue();
  }

  private MessageEmbed getMixerEmbed(MixerEqualizer mixer){
    EmbedBuilder builder = new EmbedBuilder();
    builder.setAuthor("Mixer:");
    builder.addField("Lows", "```" + mixer.getLows() + "```", true);
    builder.addField("Mids", "```" + mixer.getMids() + "```", true);
    builder.addField("Highs", "```" + mixer.getHighs() + "```", true);
    builder.setFooter("Type /mixer reset to reset the mixer to the default values");
    return builder.build();
  }

  private List<ActionRow> getMixerDropdowns(MixerEqualizer mixer){
    SelectionMenu lowMenu = SelectionMenu.create(DROPDOWN_LOW).setPlaceholder("LOWS")
        .addOptions(mixer.getRange().stream().map(v ->
            SelectOption.of((v > 0 ? "+" : "") + v, "low_" + v)
        ).collect(Collectors.toList())).build();
    SelectionMenu midMenu = SelectionMenu.create(DROPDOWN_MID).setPlaceholder("MIDS")
        .addOptions(mixer.getRange().stream().map(v ->
            SelectOption.of((v > 0 ? "+" : "") + v, "mid_" + v)
        ).collect(Collectors.toList())).build();
    SelectionMenu highMenu = SelectionMenu.create(DROPDOWN_HIGH).setPlaceholder("HIGHS")
        .addOptions(mixer.getRange().stream().map(v ->
            SelectOption.of((v > 0 ? "+" : "") + v, "high_" + v)
        ).collect(Collectors.toList())).build();
    registerDropdown(lowMenu);
    registerDropdown(midMenu);
    registerDropdown(highMenu);
    return List.of(ActionRow.of(lowMenu), ActionRow.of(midMenu), ActionRow.of(highMenu));
  }

  private void reset(MixerEqualizer mixer){
    mixer.setLows(0);
    mixer.setMids(0);
    mixer.setHighs(0);
  }

  public void apply(Guild guild){
    PlayerManager playerManager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = playerManager.getGuildAudioManager(guild);
    MixerEqualizer mixer = guildAudioManager.getMixer();
    mixer.reapplyEqualizer();
  }
}
