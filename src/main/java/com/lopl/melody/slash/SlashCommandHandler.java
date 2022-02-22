package com.lopl.melody.slash;

import com.lopl.melody.slash.component.ButtonManager;
import com.lopl.melody.slash.component.DropdownManager;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandHandler extends ListenerAdapter {

  /**
   * This class extends the ListenerAdapter, so this method is automatically called from the discord api.
   * This method especially is getting called whenever a user has pressed any button.
   * The according slashCommand is received from the {@link ButtonManager} and the event is passed to the
   * {@link SlashCommand#clicked(ButtonClickEvent, boolean)} method.
   * @param event the button click event data
   */
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

  /**
   * This class extends the ListenerAdapter, so this method is automatically called from the discord api.
   * This method especially is getting called whenever a user has made an action with any SelectionMenu.
   * The according slashCommand is received from the {@link DropdownManager} and the event is passed to the
   * {@link SlashCommand#dropdown(SelectionMenuEvent, boolean)} method.
   * @param event the selection menu event data
   */
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

  /**
   * This class extends the ListenerAdapter, so this method is automatically called from the discord api.
   * This method especially is getting called whenever a user has entered a slash command.
   * The event data is passed to the correct {@link SlashCommand#execute(SlashCommandEvent)} method.
   * @param event the slash command event data
   */
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
