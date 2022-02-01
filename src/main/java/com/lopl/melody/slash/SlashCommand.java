package com.lopl.melody.slash;

import com.jagrosh.jdautilities.command.Command.Category;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Slash Command
 * General functionality is defined here
 * When entering a slash command in a guild chat, you are able to control the functionality of the command here.
 */
public abstract class SlashCommand {

  protected String name;
  protected Category category;
  protected String help;
  protected String description;

  /**
   * This will fire whenever a command reload is executed. this defines how the command is build.
   *
   * @param cca the object the command is build with. you can modify it as you want.
   * @return the finished command builder
   */
  protected CommandCreateAction onUpsert(CommandCreateAction cca) {
    return cca;
  }

  protected void onBotStart(){

  }

  protected void onJDAReady(JDA jda){

  }


  /**
   * This will fire whenever a user enters a slash command in a guild textchannel.
   *
   * @param event all the event data
   */
  protected abstract void execute(SlashCommandEvent event);

  /**
   * This will fire when a user clicked on a button regarding this command.
   * The Button has to be registered beforehand with {@link #registerButton(Button)}.
   *
   * @param event     all the button click event data
   * @param anonymous is true if the button was not cached and the class was found only with the ID
   */
  protected void clicked(ButtonClickEvent event, boolean anonymous) {

  }

  /**
   * This will fire when a user selected a option from a dropdown regarding this command.
   * The Dropdown has to be registered beforehand with {@link #registerDropdown(SelectionMenu)}.
   *
   * @param event     all the dropdown event data
   * @param anonymous is true if the dropdown was not cached and the class was found only with the ID
   */
  protected void dropdown(SelectionMenuEvent event, boolean anonymous) {

  }

  @Nullable
  public List<String> allowAnonymousComponentCall() {
    return null;
  }

  @Nullable
  public ListenerAdapter getCommandEventListener() {
    return null;
  }

  /**
   * You can register a created button with this.
   * When this button is getting clicked by a user, the {@link #clicked(ButtonClickEvent, boolean)} method will be called
   *
   * @param button the created button
   */
  protected final void registerButton(Button button) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    slashCommandClient.buttonManager.cache(button, this);
//    Logging.debug(getClass(), null, null, "registered new button");
  }

  /**
   * You can register a created dropdown with this.
   * When this dropdown is getting a new value selected by a user, the {@link #dropdown(SelectionMenuEvent, boolean)} method will be called
   *
   * @param dropdown the created dropdown
   */
  protected final void registerDropdown(SelectionMenu dropdown) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    slashCommandClient.dropdownManager.cache(dropdown, this);
//    Logging.debug(getClass(), null, null, "Registered new dropdown");
  }

  public String getName() {
    return name;
  }

  public Category getCategory() {
    return category;
  }

  public String getHelp() {
    return help;
  }

  public String getDescription() {
    return description;
  }

}
