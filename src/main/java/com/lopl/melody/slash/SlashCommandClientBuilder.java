package com.lopl.melody.slash;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for {@link SlashCommandClient}.
 */
public class SlashCommandClientBuilder {

  public final List<SlashCommand> commands;
  public final List<SlashCommand> eventListenerCommands;

  public SlashCommandClientBuilder() {
    commands = new ArrayList<>();
    eventListenerCommands = new ArrayList<>();
  }

  /**
   * Register a command for the client. After built, the command can be executed.
   * @param command a single command
   */
  public void addCommand(SlashCommand command) {
    commands.add(command);
  }

  /**
   * Register a single command or multiple for the client. After built, the commands can be executed.
   * @param command one or more commands
   */
  public void addCommands(SlashCommand... command) {
    commands.addAll(Arrays.asList(command));
  }

  /**
   * Register a command for the client. After built, the command can receive events.
   * To receive events the {@link SlashCommand#getCommandEventListener()} method has to return a {@link ListenerAdapter}.
   * @param command a single command
   * @param jda the jda builder
   */
  public void addEventListener(JDABuilder jda, SlashCommand command) {
    ListenerAdapter listenerAdapter = command.getCommandEventListener();
    if (listenerAdapter != null) {
      jda.addEventListeners(listenerAdapter);
      eventListenerCommands.add(command);
    }
  }

  /**
   * Register a single command or multiple for the client. After built, the command can receive events.
   * To receive events the {@link SlashCommand#getCommandEventListener()} method has to return a {@link ListenerAdapter}.
   * @param commands one or more commands
   * @param jda the jda builder
   */
  public void addEventListeners(JDABuilder jda, SlashCommand... commands) {
    for (SlashCommand command : commands)
      addEventListener(jda, command);
  }

  /**
   * Builds the SlashCommandClient from this instance.
   * @return a new built SlashCommandClient
   */
  public SlashCommandClient build() {
    return new SlashCommandClient(commands.toArray(SlashCommand[]::new));
  }

}
