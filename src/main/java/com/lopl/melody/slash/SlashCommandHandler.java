package com.lopl.melody.slash;

import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandHandler extends ListenerAdapter {

  @Override
  public void onButtonClick(@NotNull ButtonClickEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByButton(event.getButton());
    boolean anonymous = false;
    if (command == null) {
      if (event.getButton() == null) return;
      String id = event.getButton().getId();
      command = slashCommandClient.anonymousComponentManager.request(id);
      anonymous = true;
    }
    if (command == null) {
      Logging.debug(getClass(), event.getGuild(), null, "Found no associated Button command... sad.");
      return;
    }
    command.clicked(event, anonymous);
  }

  @Override
  public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByDropdown(event.getComponent());
    boolean anonymous = false;
    if (command == null) {
      if (event.getSelectionMenu() == null) return;
      String id = event.getSelectionMenu().getId();
      command = slashCommandClient.anonymousComponentManager.request(id);
      anonymous = true;
    }
    if (command == null) {
      Logging.debug(getClass(), event.getGuild(), null, "Found no associated SelectionMenu command... sad.");
      return;
    }
    command.dropdown(event, anonymous);
  }

  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByKeyword(event.getName());
    if (command == null) {
      Logging.debug(getClass(), event.getGuild(), null, "Found no associated command for command " + event.getCommandString() + "... This is bad!");
      return;
    }

    command.execute(event);
  }
}
