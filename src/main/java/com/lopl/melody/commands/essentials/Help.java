package com.lopl.melody.commands.essentials;

import com.lopl.melody.audioCore.slash.SlashCommand;
import com.lopl.melody.audioCore.slash.SlashCommands;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.annotation.NoUserCommand;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Help extends SlashCommand {

  public static final String HELP_COMMAND = "help_command";
  public static final String COMMAND_DROPDOWN = "command_dropdown";

  public Help() {
    super.name = "help";
    super.category = new Command.Category("Essentials");
    super.description = "shows a list of all com.lopl.melody.commands with a description";
    super.help = "/help : shows this help message";
  }

  @Override
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca.addOptions(
        new OptionData(OptionType.STRING, HELP_COMMAND, "The command, you want more information about")
            .addChoices(getChoices()));
  }

  @Nullable
  @Override
  public List<String> allowAnonymousComponentCall() {
    return List.of(COMMAND_DROPDOWN);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Logging.slashCommand(getClass(), event);

    OptionMapping option = event.getOption(HELP_COMMAND);
    String subcommand = option == null ? null : option.getAsString();
    SlashCommand helpCommand;
    if (subcommand != null && (helpCommand = getCommandByName(subcommand)) != null) {
      MessageEmbed embed = getHelpMessage(helpCommand);
      SelectionMenu menu = SelectionMenu.create(COMMAND_DROPDOWN)
          .setRequiredRange(1, 1)
          .addOptions(SelectOption.of("Overview", "null").withDefault(false))
          .addOptions(getOptions(helpCommand)).build();
      event.replyEmbeds(embed)
          .addActionRow(menu)
          .queue();
      registerDropdown(menu);
      return;
    }
    List<Category> categories = getCategories();
    MessageEmbed embed = getHelpMessage(categories);
    SelectionMenu menu = SelectionMenu.create(COMMAND_DROPDOWN)
        .setRequiredRange(1, 1)
        .addOptions(SelectOption.of("Overview", "null").withDefault(true))
        .addOptions(getOptions()).build();
    event.replyEmbeds(embed)
        .addActionRow(menu)
        .queue();
    registerDropdown(menu);
  }

  @Override
  protected void dropdown(SelectionMenuEvent event, boolean anonymous) {
    Logging.dropdown(getClass(), event);

    List<SelectOption> selection = event.getInteraction().getSelectedOptions();
    if (selection == null || selection.isEmpty())
      return;

    String selected = selection.get(0).getValue();
    if (selected.equals("null")) {
      List<Category> categories = getCategories();
      MessageEmbed embed = getHelpMessage(categories);
      event.getMessage().editMessageEmbeds(embed).queue();
    } else {
      SlashCommand command = getCommandByName(selected);
      if (command == null) return;
      MessageEmbed embed = getHelpMessage(command);
      event.getMessage().editMessageEmbeds(embed).queue();
    }
    if (event.getSelectionMenu() == null) return;
    event.editSelectionMenu(event.getSelectionMenu().createCopy().setDefaultValues(Collections.singletonList(selected)).build()).queue();

  }

  private List<Choice> getChoices() {
    return SlashCommands.getCommands().stream()
        .filter(i -> i.getName() != null && i.getHelp() != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .map(i -> new Choice(i.getName(), i.getName()))
        .collect(Collectors.toList());
  }

  private List<SelectOption> getOptions() {
    return SlashCommands.getCommands().stream()
        .filter(i -> i.getName() != null && i.getHelp() != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .map(i -> SelectOption.of(i.getName(), i.getName()))
        .collect(Collectors.toList());
  }

  private List<SelectOption> getOptions(SlashCommand selected) {
    return SlashCommands.getCommands().stream()
        .filter(i -> i.getName() != null && i.getHelp() != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .map(i -> SelectOption.of(i.getName(), i.getName()).withDefault(selected.getName().equals(i.getName())))
        .collect(Collectors.toList());
  }

  @Nullable
  private SlashCommand getCommandByName(String name) {
    AtomicReference<SlashCommand> out = new AtomicReference<>();
    SlashCommands.getCommands().stream()
        .filter(i -> i.getName() != null && i.getName().equals(name) && i.getHelp() != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .findFirst().ifPresent(out::set);
    return out.get();
  }

  private MessageEmbed getHelpMessage(List<Category> categories) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    for (Category category : categories) {
      embedBuilder.addField(category.name, getCategoryString(category), true);
    }
    embedBuilder.setAuthor("Melody's com.lopl.melody.commands:");
    embedBuilder.setFooter("Type /help followed by a command from the list to learn more about a specific command");
    return embedBuilder.build();
  }

  private MessageEmbed getHelpMessage(SlashCommand slashCommand) {
    return new EmbedBuilder().setDescription(getCommandHelp(slashCommand))
        .setAuthor(slashCommand.getName())
        .setFooter(slashCommand.getCategory().getName()).build();

  }

  private String getCategoryString(Category category) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("```");
    for (SlashCommand slashCommand : category.commands) {
      stringBuilder.append("/").append(slashCommand.getName()).append("\n");
    }
    stringBuilder.append("```");
    return stringBuilder.toString();
  }

  private String getCommandHelp(SlashCommand command) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String line : command.getHelp().split("\n")) {
      stringBuilder.append("```").append(line).append("```").append("\n");
    }
    return stringBuilder.toString();
  }

  private List<Category> getCategories() {
    List<Category> categories = new ArrayList<>();
    SlashCommands.getCommands().stream()
        .filter(i -> i.getName() != null && i.getHelp() != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .forEach(i -> {
          Category cat = null;
          for (Category category : categories)
            if (category.name.equals(i.getCategory().getName())) {
              cat = category;
              break;
            }
          if (cat != null) {
            cat.commands.add(i);
          } else {
            Category newCategory = new Category(i.getCategory().getName());
            newCategory.commands.add(i);
            categories.add(newCategory);

          }
        });
    return categories;
  }

  static class Category {
    public final List<SlashCommand> commands;
    public final String name;

    public Category(String name) {
      this.name = name;
      commands = new ArrayList<>();
    }
  }

}
