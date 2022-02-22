package com.lopl.melody.slash;

import com.lopl.melody.Melody;
import com.lopl.melody.Token;
import com.lopl.melody.slash.component.AnonymousComponentManager;
import com.lopl.melody.slash.component.ButtonManager;
import com.lopl.melody.slash.component.DropdownManager;
import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.annotation.NoUserCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class SlashCommandClient extends ListenerAdapter {
  public static SlashCommandClient INSTANCE;

  public final SlashCommand[] slashCommands;
  public final ButtonManager buttonManager;
  public final DropdownManager dropdownManager;
  public final AnonymousComponentManager anonymousComponentManager;

  SlashCommandClient(SlashCommand[] slashCommands) {
    this.slashCommands = slashCommands;
    this.buttonManager = new ButtonManager();
    this.dropdownManager = new DropdownManager();
    this.anonymousComponentManager = new AnonymousComponentManager();
    INSTANCE = this;
  }

  public static SlashCommandClient getInstance() {
    if (INSTANCE != null) return INSTANCE;
    return new SlashCommandClient(new SlashCommand[0]);
  }

  public void start() {
    Arrays.stream(slashCommands).forEach(SlashCommand::onBotStart);
  }

  public void ready(JDA jda) {
    Arrays.stream(slashCommands).forEach(sc -> sc.onJDAReady(jda));
    anonymousComponentManager.cache(Arrays.asList(slashCommands));
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

  public SlashCommand getCommandByDropdown(SelectionMenu selectionMenu) {
    return dropdownManager.request(selectionMenu);
  }

  /////////////////////////////////////////////////////////////
  //                       UPSERTER                          //
  /////////////////////////////////////////////////////////////

  public static void main(String[] args) throws LoginException, InterruptedException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Updating Commands"));
    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommands(SlashCommands.getCommands().toArray(SlashCommand[]::new));
    slashCommandClientBuilder.build();
    Melody.manager = builder.build();
    Melody.manager.awaitReady();
    upsertAllGuildsCommands(Melody.manager.getGuilds().toArray(Guild[]::new));
  }

  public static void upsertAllGuildsCommands(Guild[] guilds) {
    Melody.manager.updateCommands().queue();  // delete all global commands
    SlashCommand[] slashCommands = SlashCommands.getCommands().stream()
        .filter(i -> i.name != null && i.description != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .toArray(SlashCommand[]::new);
    upsertGuildRecursive(guilds, slashCommands, 0, (g, c) -> {
      Melody.manager.shutdown();
      Logging.info(getInstance().getClass(), null, null, "Reloading " + g + " guilds finished");
    });
  }

  public static void upsertGuildRecursive(Guild[] guilds, SlashCommand[] slashCommands, int index, Reload callback) {
    Guild guild = guilds[index];
    Logging.info(getInstance().getClass(), guild, null, "Reloading [" + guild.getName() + "]s commands...");
    guild.updateCommands().queue();
    upsertCommandsRecursive(guild, slashCommands, 0, commands -> {
      Logging.info(getInstance().getClass(), guild, null, "Loaded " + slashCommands.length + " commands");
      Logging.debug(getInstance().getClass(), guild, null, "Commands are: " + Arrays.toString(Arrays.stream(slashCommands).map(sc -> sc.name).toArray()));
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
