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

/**
 * Manager class for all {@link SlashCommand}s, {@link Button}s and {@link SelectionMenu}s.
 * An instance can be built with the {@link SlashCommandClientBuilder}.
 * The class implements the Singleton design pattern in some way.
 * Meaning, that once a instance is created you can retrieve this instance with {@link #getInstance()}.
 */
public class SlashCommandClient extends ListenerAdapter {
  public static SlashCommandClient INSTANCE;

  public final SlashCommand[] slashCommands;
  public final ButtonManager buttonManager;
  public final DropdownManager dropdownManager;
  public final AnonymousComponentManager anonymousComponentManager;
  public final AutomaticCommandUpsert automaticCommandUpsert;

  /**
   * package-private constructor for the {@link SlashCommandClientBuilder}.
   * @param slashCommands all the registered commands
   */
  SlashCommandClient(SlashCommand[] slashCommands) {
    this.slashCommands = slashCommands;
    this.buttonManager = new ButtonManager();
    this.dropdownManager = new DropdownManager();
    this.anonymousComponentManager = new AnonymousComponentManager();
    this.automaticCommandUpsert = new AutomaticCommandUpsert();
    INSTANCE = this;
  }

  /**
   * Getter method for the Singleton Pattern.
   * @return cached instance or new one
   */
  public static SlashCommandClient getInstance() {
    if (INSTANCE != null) return INSTANCE;
    return new SlashCommandClient(new SlashCommand[0]);
  }

  /**
   * This calls all {@link SlashCommand#onBotStart()} methods.
   */
  public void start() {
    Arrays.stream(slashCommands).forEach(SlashCommand::onBotStart);
  }

  /**
   * This calls all {@link SlashCommand#onJDAReady(JDA)} methods.
   */
  public void ready(JDA jda) {
    Arrays.stream(slashCommands).forEach(sc -> sc.onJDAReady(jda));
    anonymousComponentManager.cache(Arrays.asList(slashCommands));
    automaticCommandUpsert.upsert(jda.getGuilds(), Arrays.asList(slashCommands));
    jda.addEventListener(automaticCommandUpsert);
  }

  /**
   * This returns a SlashCommand for a given keyword.
   * If the keyword matches any of the SlashCommand names, the first one is returned
   * @param keyword a {@link SlashCommand#name}
   * @return the belonging SlashCommand
   */
  public SlashCommand getCommandByKeyword(String keyword) {
    for (SlashCommand slashCommand : slashCommands) {
      if (slashCommand.name != null && slashCommand.name.equals(keyword)) {
        return slashCommand;
      }
    }
    return null;
  }

  /**
   * This returns a SlashCommand for a registered button.
   * If the keyword matches any of the buttons in the buttonManager, the first one is returned
   * You have to use {@link SlashCommand#registerButton(Button)} with the button before.
   * @param button a registered {@link Button}
   * @return the belonging SlashCommand
   */
  public SlashCommand getCommandByButton(Button button) {
    return buttonManager.request(button);
  }

  /**
   * This returns a SlashCommand for a registered dropdown.
   * If the keyword matches any of the dropdowns in the dropdownManager, the first one is returned
   * You have to use {@link SlashCommand#registerDropdown(SelectionMenu)} with the selectionMenu before.
   * @param selectionMenu a registered {@link SelectionMenu}
   * @return the belonging SlashCommand
   */
  public SlashCommand getCommandByDropdown(SelectionMenu selectionMenu) {
    return dropdownManager.request(selectionMenu);
  }

}
