package com.lopl.melody.slash;

import com.lopl.melody.Token;
import com.lopl.melody.utils.Logging;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;

public class SlashCommandUpsertGlobal {

  private static JDA manager;

  /**
   * Execute this method to reload all commands and their arguments.
   * This method will upsert globally.
   * This takes some time for discord to handle the query. (up to 24 hrs)
   *
   * @param args should be an empty array
   * @throws LoginException       if the discord api is not available
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
    manager.updateCommands().queue();
    Logging.info(SlashCommandUpsertLocal.class, null, null, "Sent command query to discord");
    upsertAllGuildsCommands(manager.getGuilds().toArray(Guild[]::new));
  }

  public static void upsertAllGuildsCommands(Guild[] guilds) {
    manager.updateCommands().queue();  // delete all global commands
    upsertGuildRecursive(guilds, 0, g -> {
      manager.shutdown();
      Logging.info(SlashCommandUpsertLocal.class, null, null, "Deleted " + g + " local guild commands");
    });
  }

  public static void upsertGuildRecursive(Guild[] guilds, int index, Reload callback) {
    Guild guild = guilds[index];
    guild.updateCommands().complete();
    int newIndex = index + 1;
    if (newIndex >= guilds.length) {
      if (callback != null)
        callback.onFinish(newIndex);
      return;
    }
    upsertGuildRecursive(guilds, newIndex, callback);
  }

  interface Reload {
    void onFinish(int guilds);
  }

}
