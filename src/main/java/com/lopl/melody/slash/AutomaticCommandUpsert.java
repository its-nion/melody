package com.lopl.melody.slash;

import com.lopl.melody.utils.Logging;
import com.lopl.melody.utils.annotation.NoUserCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AutomaticCommandUpsert extends ListenerAdapter {

  public void upsert(List<Guild> guilds, List<SlashCommand> commands) {
    for (Guild guild : guilds)
      upsertMissingCommands(guild, commands);
  }

  private void upsertMissingCommands(Guild guild, List<SlashCommand> commands){
    Logging.debug(getClass(), guild, null, "(Re-)Uploading missing commands...");
    commands = commands.stream()
        .filter(i -> i.name != null && i.description != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .collect(Collectors.toList());
    List<Command> loadedCommands = guild.retrieveCommands().complete();
    int guildCommandCount = 0;
    for (SlashCommand command : commands){
      if (loadedCommands.stream().noneMatch(c -> c.getName().equals(command.name))){
        CommandCreateAction cca = guild.upsertCommand(command.name, command.description);
        cca = command.onUpsert(cca);
        cca.complete();
        guildCommandCount++;
      }
    }
    if (guildCommandCount > 0)
      Logging.info(getClass(), guild, null, "Loaded " + guildCommandCount + " new commands to this guild");

  }

  @Override
  public void onGuildJoin(@NotNull GuildJoinEvent event) {
    SlashCommand[] slashCommands = SlashCommands.getCommands().stream()
        .filter(i -> i.name != null && i.description != null && !i.getClass().isAnnotationPresent(NoUserCommand.class))
        .toArray(SlashCommand[]::new);
    upsertMissingCommands(event.getGuild(), Arrays.asList(slashCommands));
  }
}
