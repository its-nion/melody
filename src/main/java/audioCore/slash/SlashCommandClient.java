package audioCore.slash;

import melody.Main;
import melody.Token;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import javax.security.auth.login.LoginException;

public class SlashCommandClient extends ListenerAdapter {
  public static SlashCommandClient INSTANCE;

  public SlashCommand[] slashCommands;
  public ButtonManager buttonManager;

  public static SlashCommandClient getInstance(){
    return INSTANCE;
  }

  public SlashCommandClient(SlashCommand[] slashCommands) {
    this.slashCommands = slashCommands;
    this.buttonManager = new ButtonManager();
    INSTANCE = this;
  }

  public SlashCommand getCommandByKeyword(String keyword){
    for (SlashCommand slashCommand : slashCommands){
      if (slashCommand.name.equals(keyword)) {
        return slashCommand;
      }
    }
    return null;
  }

  public SlashCommand getCommandByButton(Button button){
    return buttonManager.request(button);
  }

  public static void main(String[] args) throws LoginException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "reloading"));
    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommands(SlashCommands.commandMap.values().toArray(SlashCommand[]::new));
    slashCommandClientBuilder.build();
    Main.manager = builder.build();
    upsertCommands();
  }

  public static void upsertCommands(){
    Main.manager.updateCommands().queue();
    for (SlashCommand slashCommand : getInstance().slashCommands){
      CommandCreateAction cca = Main.manager.upsertCommand(slashCommand.name, slashCommand.description);
      System.out.printf("Command '%s' : %s \n", slashCommand.name, slashCommand.description);
      cca = slashCommand.onUpsert(cca);
      cca.queue();
    }
    System.out.printf("Queued an update for all %d commands. This will take a while. Requester (This instance) will shutdown in 2 min... \n", getInstance().slashCommands.length);
    new Thread(() -> {
      try {
        Thread.sleep(1000 * 60 * 2);
      } catch (InterruptedException ignored){
      } finally {
        Main.manager.shutdown();
      }
    }).start();
  }

}
