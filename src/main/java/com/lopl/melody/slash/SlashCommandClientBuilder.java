package com.lopl.melody.slash;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlashCommandClientBuilder {

  public List<SlashCommand> commands;
  public List<SlashCommand> eventListenerCommands;

  public SlashCommandClientBuilder() {
    commands = new ArrayList<>();
    eventListenerCommands = new ArrayList<>();
  }

  public void addCommand(SlashCommand command) {
    commands.add(command);
  }

  public void addCommands(SlashCommand... command) {
    commands.addAll(Arrays.asList(command));
  }

  public void addEventListener(JDABuilder jda, SlashCommand command) {
    ListenerAdapter listenerAdapter = command.getCommandEventListener();
    if (listenerAdapter != null) {
      jda.addEventListeners(listenerAdapter);
      eventListenerCommands.add(command);
    }
  }

  public void addEventListeners(JDABuilder jda, SlashCommand... commands) {
    for (SlashCommand command : commands)
      addEventListener(jda, command);
  }

  public SlashCommandClient build() {
    return new SlashCommandClient(commands.toArray(SlashCommand[]::new));
  }

}
