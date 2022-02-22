package com.lopl.melody.slash;

import com.jagrosh.jdautilities.command.Command.Category;
import com.lopl.melody.Melody;
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

  /**
   * A name is required for every SlashCommand to being able to be executed via discord.
   */
  protected String name;

  /**
   * The category will define in which Category the command is sorted.
   */
  protected Category category;

  /**
   * The help String will get displayed when the user triggers the help Command with this specific command.
   * For a simple implementation this String can be declared with triple quotes to keep line breaks easy.
   */
  protected String help;

  /**
   * The description should give a short explanation of the command. There is a max character limit
   */
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

  /**
   * This will fire when the bot is started and the command is loaded.
   * The JDA object at {@link Melody#manager} is not ready yet.
   */
  protected void onBotStart() {

  }

  /**
   * This will fire when the JDA object at {@link Melody#manager} is ready.
   */
  protected void onJDAReady(JDA jda) {

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

  /**
   * Some Buttons and SelectionMenus allow to execute the specific action,
   * even if the message is really old. To keep track of all the ids of the Components, that support this,
   * a list of ids is created.
   * Override this method to add ids to this List.
   * @return a List with always working Component ids.
   */
  @Nullable
  public List<String> allowAnonymousComponentCall() {
    return null;
  }

  /**
   * If a Command needs to subscribe to any event provided by the discord api,
   * You can create a subclass in your Command, that extends the {@link ListenerAdapter}.
   * Override this method and return an instance of this class here to register the listener.
   * @return any instance of an ListenerAdapter.
   */
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
