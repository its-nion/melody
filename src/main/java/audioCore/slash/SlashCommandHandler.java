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
    if (command == null) return; // TODO try to get class with the public static defined ids in the command classes

    command.clicked(event);
  }

  @Override
  public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByDropdown(event.getComponent());
    if (command == null) return; // TODO try to get class with the public static defined ids in the command classes

    command.dropdown(event);
  }

  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByKeyword(event.getName());
    if (command == null) return;

    command.execute(event);
  }
}
