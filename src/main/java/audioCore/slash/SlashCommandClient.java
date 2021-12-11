package audioCore.slash;

import melody.Main;
import melody.Token;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
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
      if (slashCommand.name != null && slashCommand.name.equals(keyword)) {
        return slashCommand;
      }
    }
    return null;
  }

  public SlashCommand getCommandByButton(Button button){
    return buttonManager.request(button);
  }

  public static void main(String[] args) throws LoginException, InterruptedException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Updating Commands"));
    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommands(SlashCommands.commandMap.values().toArray(SlashCommand[]::new));
    slashCommandClientBuilder.build();
    Main.manager = builder.build();
    Main.manager.awaitReady();
    upsertCommands();
  }

  public static void upsertCommands(){
    Main.manager.updateCommands().queue();
//    for (SlashCommand slashCommand : SlashCommands.commandMap.values().stream().filter(i -> i.name != null).toArray(SlashCommand[]::new)){
//      CommandCreateAction cca = Main.manager.upsertCommand(slashCommand.name, slashCommand.description);
//      System.out.printf("[Global] Command '%s' : %s \n", slashCommand.name, slashCommand.description);
//      cca = slashCommand.onUpsert(cca);
//      cca.queue();
//    }
    for (Guild guild : Main.manager.getGuilds())
      upsertCommands(guild);
    System.out.printf("Queued an update for %d commands. This will take a while. Requester (This instance) will shutdown in 20 sec... \n", SlashCommands.commandMap.values().stream().filter(i -> i.name != null).toArray(SlashCommand[]::new).length);
    new Thread(() -> {
      try {
        Thread.sleep(1000 * 20);
      } catch (InterruptedException ignored){
      } finally {
        Main.manager.shutdown();
      }
    }).start();
  }

  public static void upsertCommands(Guild guild){
    guild.updateCommands().queue();
    for (SlashCommand slashCommand : SlashCommands.commandMap.values().stream().filter(i -> i.name != null).toArray(SlashCommand[]::new)){
      CommandCreateAction cca = guild.upsertCommand(slashCommand.name, slashCommand.description);
      System.out.printf("[%s] Command '%s' : %s \n", guild.getName(), slashCommand.name, slashCommand.description);
      cca = slashCommand.onUpsert(cca);
      cca.queue();
    }
  }

}
