package com.lopl.melody.slash;

import com.lopl.melody.Token;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.annotation.NoUserCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class SlashCommandUpsertLocal {

  private static JDA manager;

  /**
   * Execute this method to reload all commands and their arguments.
   * This method will upsert every guild after another.
   * This takes some time to finish, depending on how many guilds the bot is in.
   * You could upsert the commands globally, but this can take up to 24 hours till discord manages the request.
   * All following methods work recursive with finished callbacks to avoid await statements.
   * @param args should be an empty array
   * @throws LoginException if the discord api is not available
   * @throws InterruptedException if the user cancels the program or the manager doesn't finish
   */
  public static void main(String[] args) throws LoginException, InterruptedException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Updating Commands"));
    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommands(SlashCommands.getCommands().toArray(SlashCommand[]::new));
    slashCommandClientBuilder.build();
    manager = builder.build();
    manager.awaitReady();
    upsertAllGuildsCommands(manager.getGuilds().toArray(Guild[]::new));
  }

  public static void upsertAllGuildsCommands(Guild[] guilds) {
    manager.updateCommands().queue();  // delete all global commands
    SlashCommand[] slashCommands = SlashCommands.getCommands().stream()
        .filter(i -> i.name != null && i.description != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .toArray(SlashCommand[]::new);
    upsertGuildRecursive(guilds, slashCommands, 0, (g, c) -> {
      manager.shutdown();
      Logging.info(SlashCommandUpsertLocal.class, null, null, "Reloading " + g + " guilds finished");
    });
  }

  public static void upsertGuildRecursive(Guild[] guilds, SlashCommand[] slashCommands, int index, Reload callback) {
    Guild guild = guilds[index];
    Logging.info(SlashCommandUpsertLocal.class, guild, null, "Reloading [" + guild.getName() + "]s commands...");
    guild.updateCommands().queue();
    upsertCommandsRecursive(guild, slashCommands, 0, commands -> {
      Logging.info(SlashCommandUpsertLocal.class, guild, null, "Loaded " + slashCommands.length + " commands");
      Logging.debug(SlashCommandUpsertLocal.class, guild, null, "Commands are: " + Arrays.toString(Arrays.stream(slashCommands).map(sc -> sc.name).toArray()));
      int newIndex = index + 1;
      if (newIndex >= guilds.length) {
        if (callback != null)
          callback.onFinish(newIndex, slashCommands.length);
        return;
      }
      upsertGuildRecursive(guilds, slashCommands, newIndex, callback);
    });

  }

  public static void upsertCommandsRecursive(Guild guild, SlashCommand[] commands, int index, GuildReload callback) {
    SlashCommand slashCommand = commands[index];
    CommandCreateAction cca = guild.upsertCommand(slashCommand.name, slashCommand.description);
    cca = slashCommand.onUpsert(cca);
    upsertGuildCommand(cca, () -> {
      int newIndex = index + 1;
      if (newIndex >= commands.length) {
        if (callback != null)
          callback.onFinish(newIndex);
        return;
      }
      upsertCommandsRecursive(guild, commands, newIndex, callback);
    });
  }

  public static void upsertGuildCommand(CommandCreateAction cca, GuildCommandReload commandReload){
    cca.queue(c -> commandReload.onFinish());
  }

  interface GuildReload {
    void onFinish(int amount);
  }

  interface Reload {
    void onFinish(int guilds, int commands);
  }

  interface GuildCommandReload{
    void onFinish();
  }

}
