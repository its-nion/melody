package com.lopl.melody.slash;

import com.lopl.melody.testutils.GuildCreator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SlashCommandHandlerTest {

  @Mock ButtonClickEvent buttonClickEvent;
  @Mock SelectionMenuEvent selectionMenuEvent;
  @Mock SlashCommandEvent slashCommandEvent;

  SlashCommandClient slashCommandClient;

  @BeforeEach
  void setUp() {
    SlashCommandClientBuilder sccb = new SlashCommandClientBuilder();
    sccb.addCommand(slashCommand);
    slashCommandClient = sccb.build();
  }

  @Test
  void onButtonClickCached() {
    assertNotNull(buttonClickEvent);
    Guild guild = GuildCreator.create(123456789);
    Button button = Button.secondary("test_id", "Test of SlashCommandHandler");
    slashCommandClient.buttonManager.cache(button, slashCommand);
    when(buttonClickEvent.getGuild()).thenReturn(guild);
    when(buttonClickEvent.getButton()).thenReturn(button);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onButtonClick(buttonClickEvent);
  }

  @Test
  void onButtonClickAnonymous() {
    assertNotNull(buttonClickEvent);
    Guild guild = GuildCreator.create(123456789);
    Button button = Button.secondary("test_id", "Test of SlashCommandHandler");
    if (!slashCommandClient.anonymousComponentManager.contains("test_id"))
      slashCommandClient.anonymousComponentManager.cache("test_id", slashCommand);
    when(buttonClickEvent.getGuild()).thenReturn(guild);
    when(buttonClickEvent.getButton()).thenReturn(button);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onButtonClick(buttonClickEvent);
  }

  @Test
  void onButtonClickUnknown() {
    assertNotNull(buttonClickEvent);
    Guild guild = GuildCreator.create(123456789);
    Button button = Button.secondary("test_id", "Test of SlashCommandHandler");
    when(buttonClickEvent.getGuild()).thenReturn(guild);
    when(buttonClickEvent.getButton()).thenReturn(button);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onButtonClick(buttonClickEvent);
  }

  @Test
  void onSelectionMenuCached() {
    assertNotNull(selectionMenuEvent);
    Guild guild = GuildCreator.create(123456789);
    SelectionMenu selectionMenu = SelectionMenu.create("test_id").build();
    slashCommandClient.dropdownManager.cache(selectionMenu, slashCommand);
    when(selectionMenuEvent.getGuild()).thenReturn(guild);
    when(selectionMenuEvent.getSelectionMenu()).thenReturn(selectionMenu);
    when(selectionMenuEvent.getComponent()).thenReturn(selectionMenu);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onSelectionMenu(selectionMenuEvent);
  }

  @Test
  void onSelectionMenuAnonymous() {
    assertNotNull(selectionMenuEvent);
    Guild guild = GuildCreator.create(123456789);
    SelectionMenu selectionMenu = SelectionMenu.create("test_id").build();
    if (!slashCommandClient.anonymousComponentManager.contains("test_id"))
      slashCommandClient.anonymousComponentManager.cache("test_id", slashCommand);
    when(selectionMenuEvent.getGuild()).thenReturn(guild);
    when(selectionMenuEvent.getSelectionMenu()).thenReturn(selectionMenu);
    when(selectionMenuEvent.getComponent()).thenReturn(selectionMenu);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onSelectionMenu(selectionMenuEvent);
  }

  @Test
  void onSelectionMenuUnknown() {
    assertNotNull(selectionMenuEvent);
    Guild guild = GuildCreator.create(123456789);
    SelectionMenu selectionMenu = SelectionMenu.create("test_id").build();
    when(selectionMenuEvent.getGuild()).thenReturn(guild);
    when(selectionMenuEvent.getSelectionMenu()).thenReturn(selectionMenu);
    when(selectionMenuEvent.getComponent()).thenReturn(selectionMenu);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onSelectionMenu(selectionMenuEvent);
  }

  @Test
  void onSlashCommand() {
    assertNotNull(slashCommandEvent);
    slashCommand.name = "testcommand";
    Guild guild = GuildCreator.create(123456789);
    when(slashCommandEvent.getName()).thenReturn("testcommand");
    when(slashCommandEvent.getGuild()).thenReturn(guild);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onSlashCommand(slashCommandEvent);
  }

  @Test
  void onSlashCommandUnknown() {
    assertNotNull(slashCommandEvent);
    slashCommand.name = "testcommand";
    Guild guild = GuildCreator.create(123456789);
    when(slashCommandEvent.getName()).thenReturn("testcommand_unknown");
    when(slashCommandEvent.getGuild()).thenReturn(guild);
    SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    slashCommandHandler.onSlashCommand(slashCommandEvent);
  }

  static SlashCommand slashCommand = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Override
    protected void clicked(ButtonClickEvent event, boolean anonymous) {

    }

    @Override
    protected void dropdown(SelectionMenuEvent event, boolean anonymous) {

    }

  };
}