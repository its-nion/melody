package audioCore.slash;

import melody.Main;
import melody.Token;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import utils.Logging;
import utils.annotation.NoUserCommand;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class SlashCommandClient extends ListenerAdapter {
  public static SlashCommandClient INSTANCE;

  public SlashCommand[] slashCommands;
  public ButtonManager buttonManager;
  public DropdownManager dropdownManager;

  public static SlashCommandClient getInstance() {
    return INSTANCE;
  }

  public SlashCommandClient(SlashCommand[] slashCommands) {
    this.slashCommands = slashCommands;
    this.buttonManager = new ButtonManager();
    this.dropdownManager = new DropdownManager();
    INSTANCE = this;
  }

  public SlashCommand getCommandByKeyword(String keyword) {
    for (SlashCommand slashCommand : slashCommands) {
      if (slashCommand.name != null && slashCommand.name.equals(keyword)) {
        return slashCommand;
      }
    }
    return null;
  }

  public SlashCommand getCommandByButton(Button button) {
    return buttonManager.request(button);
  }

  public SlashCommand getCommandByDropdown(SelectionMenu selectionMenu){
    return dropdownManager.request(selectionMenu);
  }

  public static void main(String[] args) throws LoginException, InterruptedException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Updating Commands"));
    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommands(SlashCommands.commandMap.values().toArray(SlashCommand[]::new));
    slashCommandClientBuilder.build();
    Main.manager = builder.build();
    Main.manager.awaitReady();
    upsertAllGuildsCommands();
  }

  public static void upsertAllGuildsCommands() {
    Main.manager.updateCommands().queue();  // delete all global commands
    SlashCommand[] slashCommands = SlashCommands.commandMap.values().stream()
        .filter(i -> i.name != null && i.description != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .toArray(SlashCommand[]::new);
    upsertGuildRecursive(Main.manager.getGuilds().toArray(Guild[]::new), slashCommands, 0, (g, c) -> {
      Main.manager.shutdown();
      Logging.info(getInstance().getClass(), null, null, "Reloading " + g + " guilds finished");
    });
  }

  public static void upsertGuildRecursive(Guild[] guilds, SlashCommand[] slashCommands, int index, CommandReload callback) {
    Guild guild = guilds[index];
    Logging.info(getInstance().getClass(), guild, null, "Reloading [" + guild.getName() + "]s commands...");
    guild.updateCommands().queue();
    upsertCommandsRecursive(guild, slashCommands, 0, commands -> {
      Logging.info(getInstance().getClass(), guild, null, "Loaded " + slashCommands.length + " commands");
      Logging.info(getInstance().getClass(), guild, null, "Commands are: " + Arrays.toString(Arrays.stream(slashCommands).map(sc -> sc.name).toArray()));
      int newIndex = index + 1;
      if (newIndex >= guilds.length){
        callback.onFinish(newIndex, slashCommands.length);
        return;
      }
      upsertGuildRecursive(guilds, slashCommands, newIndex, callback);
    });

  }

  private static void upsertCommandsRecursive(Guild guild, SlashCommand[] commands, int index, GuildCommandReload callback){
    SlashCommand slashCommand = commands[index];
    CommandCreateAction cca = guild.upsertCommand(slashCommand.name, slashCommand.description);
    cca = slashCommand.onUpsert(cca);
    cca.queue(c -> {
      int newIndex = index + 1;
      if (newIndex >= commands.length){
        callback.onFinish(newIndex);
        return;
      }
      upsertCommandsRecursive(guild, commands, newIndex, callback);
    });
  }

  private interface GuildCommandReload{
    void onFinish(int amount);
  }

  private interface CommandReload{
    void onFinish(int guilds, int commands);
  }

}
