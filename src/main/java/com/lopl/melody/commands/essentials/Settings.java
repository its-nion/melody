package com.lopl.melody.commands.essentials;

import com.jagrosh.jdautilities.command.Command;
import com.lopl.melody.slash.SlashCommand;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.embed.EmbedError;
import com.lopl.melody.settings.GuildSettings;
import com.lopl.melody.settings.Setting;
import com.lopl.melody.settings.SettingValue;
import com.lopl.melody.settings.SettingsManager;
import com.lopl.melody.settings.items.DefaultMusicType;
import com.lopl.melody.settings.items.MusicPlayerProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Settings extends SlashCommand {

  public static final List<Setting<? extends SettingValue>> settings = Arrays.asList(
      new MusicPlayerProvider(),
      new DefaultMusicType());

  public static final String SETTINGS_DROPDOWN = "settings_dropdown";

  public Settings() {
    super.name = "settings";
    super.category = new Command.Category("Essentials");
    super.description = "shows a list of all settings. You can change these here.";
    super.help = "/settings : shows a list of all settings. You can change these as you like or just have a look at them.";
  }

  @Nullable
  @Override
  public List<String> allowAnonymousComponentCall() {
    return List.of(SETTINGS_DROPDOWN);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    Guild guild = event.getGuild();
    if (event.getGuild() == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    MessageEmbed embed = getSettingsMessage(guild);
    SelectionMenu menu = getSettingsDropdown(guild);
    event.replyEmbeds(embed).addActionRow(menu).queue();
    registerDropdown(menu);
  }

  @Override
  protected void dropdown(SelectionMenuEvent event, boolean anonymous) {
    Logging.dropdown(getClass(), event);

    if (event.getGuild() == null) {
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    List<SelectOption> selection = event.getInteraction().getSelectedOptions();
    if (selection == null || selection.isEmpty())
      return;

    SettingsManager settingsManager = SettingsManager.getInstance();
    GuildSettings guildSettings = settingsManager.getGuildSettings(event.getGuild());

    String className = selection.get(0).getValue();
    Setting<SettingValue> setting;
    try {
      Class<?> sClass = Class.forName(className);
      setting = guildSettings.getUnknownSetting(sClass);
    } catch (ClassNotFoundException e) {
      Logging.debug(getClass(), event.getGuild(), null, "Settings class not found for: " + className);
      return;
    }

    SelectionMenu settingsDropdown = getSettingsDropdown(event.getGuild(), className);
    List<Button> settingButtons = getSettingButtons(setting);
    event.editComponents(
        ActionRow.of(settingsDropdown),
        ActionRow.of(settingButtons)
        ).queue();
    for (Button button : settingButtons)
      registerButton(button);
  }

  @Override
  protected void clicked(ButtonClickEvent event, boolean anonymous) {
    Logging.button(getClass(), event);

    if (event.getGuild() == null){
      event.replyEmbeds(EmbedError.with("This command can only be executed in a server textchannel")).queue();
      return;
    }

    if (event.getButton() == null || event.getButton().getId() == null){
      event.replyEmbeds(EmbedError.with("Something is wrong with this button...")).queue();
      return;
    }

    SettingsManager settingsManager = SettingsManager.getInstance();
    GuildSettings guildSettings = settingsManager.getGuildSettings(event.getGuild());

    String value = event.getButton().getId();
    String className = value.split("_")[1];
    String i = value.split("_")[2];
    Setting<SettingValue> setting;
    int data;
    try {
      Class<?> sClass = Class.forName(className);
      setting = guildSettings.getUnknownSetting(sClass);
      data = Integer.parseInt(i);
    } catch (ClassNotFoundException e) {
      Logging.debug(getClass(), event.getGuild(), null, "Settings class not found for: " + className);
      return;
    } catch (NumberFormatException e) {
      Logging.debug(getClass(), event.getGuild(), null, "Settings value error: " + className);
      return;
    }

    setting.updateData(data);
    //TODO: tell guildsettings that the data has changed so it can change the db

    MessageEmbed embed = getSettingsMessage(event.getGuild());
    event.editMessageEmbeds(embed).queue();
  }

  private MessageEmbed getSettingsMessage(Guild guild){
    SettingsManager settingsManager = SettingsManager.getInstance();
    GuildSettings guildSettings = settingsManager.getGuildSettings(guild);

    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setAuthor("Melody's settings in " + guild.getName());
    for (Setting<?> setting : guildSettings.getSettings()){
      embedBuilder.addField(
          setting.getName(), // title
          "```" + setting.getValueRepresentation() + "```", // content
          true
      );
    }
    return embedBuilder.build();
  }

  private SelectionMenu getSettingsDropdown(Guild guild) {
    return SelectionMenu.create(SETTINGS_DROPDOWN)
        .setRequiredRange(1, 1)
        .setPlaceholder("Select a setting")
        .addOptions(getOptions(guild, "")).build();
  }

  private SelectionMenu getSettingsDropdown(Guild guild, String activeClass) {
    return SelectionMenu.create(SETTINGS_DROPDOWN)
        .setRequiredRange(1, 1)
        .setPlaceholder("Select a setting")
        .addOptions(getOptions(guild, activeClass)).build();
  }

  private List<SelectOption> getOptions(Guild guild, String className) {
    SettingsManager settingsManager = SettingsManager.getInstance();
    GuildSettings guildSettings = settingsManager.getGuildSettings(guild);

    return guildSettings.getSettings().stream().map(s -> SelectOption.of(s.getName(), s.getClass().getName()).withDefault(s.getClass().getName().equals(className))).collect(Collectors.toList());
  }


  private List<Button> getSettingButtons(Setting<SettingValue> setting){
    return setting.getPossibilities().stream().map(sv ->
        Button.secondary("setting_" + setting.getClass().getName() + "_" + sv.getData(), sv.getValueRepresentation())
    ).collect(Collectors.toList());
  }
}
