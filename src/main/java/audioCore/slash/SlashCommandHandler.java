package audioCore.slash;

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
    if (command == null) return;
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
    if (command == null) return;
    command.dropdown(event, anonymous);
  }

  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByKeyword(event.getName());
    if (command == null) return;

    command.execute(event);
  }
}
