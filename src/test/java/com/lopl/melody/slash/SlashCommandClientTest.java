package com.lopl.melody.slash;

import com.lopl.melody.testutils.GuildCreator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.security.auth.login.LoginException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SlashCommandClientTest {

  @BeforeEach
  void setUp() {
    slashCommand.name = "test_id";
  }

  @Test
  void getCommandByKeyword() {
    SlashCommandClient scc = new SlashCommandClient(new SlashCommand[]{slashCommand});
    SlashCommand command = scc.getCommandByKeyword("test_id");
    SlashCommand nullCommand = scc.getCommandByKeyword("test_unknown_id");
    assertEquals(command, slashCommand);
    assertNull(nullCommand);

  }

  @Test
  void getCommandByButton() {
    SlashCommandClient scc = new SlashCommandClient(new SlashCommand[]{slashCommand});
    Button button = Button.secondary("test_id", "Test Button");
    Button nullButton = Button.secondary("test_unknown_id", "Test Button");
    scc.buttonManager.cache(button, slashCommand);
    SlashCommand command = scc.getCommandByButton(button);
    SlashCommand nullCommand = scc.getCommandByButton(nullButton);
    assertEquals(command, slashCommand);
    assertNull(nullCommand);
  }

  @Test
  void getCommandByDropdown() {
    SlashCommandClient scc = new SlashCommandClient(new SlashCommand[]{slashCommand});
    SelectionMenu selectionMenu = SelectionMenu.create("test_id").build();
    SelectionMenu nullSelectionMenu = SelectionMenu.create("test_unknown_id").build();
    scc.dropdownManager.cache(selectionMenu, slashCommand);
    SlashCommand command = scc.getCommandByDropdown(selectionMenu);
    SlashCommand nullCommand = scc.getCommandByDropdown(nullSelectionMenu);
    assertEquals(command, slashCommand);
    assertNull(nullCommand);
  }

  @Mock
  Guild guild = GuildCreator.create(123456789);
  @Mock
  CommandListUpdateAction updateAction;
  @Mock
  CommandCreateAction commandCreateAction;

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  void upsertGuildRecursive() {
    new SlashCommandClient(new SlashCommand[0]);
    assertNotNull(commandCreateAction);
    assertNotNull(updateAction);
    when(guild.updateCommands()).thenReturn(updateAction);
    when(guild.getName()).thenReturn("TestGuild");
    when(guild.upsertCommand(any(), any())).thenReturn(commandCreateAction);
    when(guild.upsertCommand(any(), eq(null))).thenReturn(commandCreateAction);
    when(guild.upsertCommand(eq(null), any())).thenReturn(commandCreateAction);
    when(guild.upsertCommand(eq(null), eq(null))).thenReturn(commandCreateAction);
    doNothing().when(updateAction).queue();
    doNothing().when(commandCreateAction).queue();
    Guild[] guilds = List.of(guild).toArray(Guild[]::new);
    SlashCommand[] slashCommands = List.of(slashCommand).toArray(SlashCommand[]::new);
    SlashCommandUpsertLocal.upsertGuildRecursive(guilds, slashCommands, 0, null);

    verify(guild).updateCommands();
    verify(updateAction).queue();

  }

  @Test
  void mainUpsertTest(){
    MockedStatic<SlashCommandClient> util = mockStatic(SlashCommandClient.class);
    util.when(SlashCommandClient::getInstance).thenCallRealMethod();
    util.when(() -> SlashCommandUpsertLocal.main(any())).thenCallRealMethod();
    util.when(() -> SlashCommandUpsertLocal.upsertAllGuildsCommands(any())).thenCallRealMethod();
    ArgumentCaptor<SlashCommandUpsertLocal.Reload> intfa = ArgumentCaptor.forClass(SlashCommandUpsertLocal.Reload.class);
    util.when(() -> SlashCommandUpsertLocal.upsertGuildRecursive(any(), any(), anyInt(), intfa.capture())).thenCallRealMethod();
    ArgumentCaptor<SlashCommandUpsertLocal.GuildReload> intfac = ArgumentCaptor.forClass(SlashCommandUpsertLocal.GuildReload.class);
    util.when(() -> SlashCommandUpsertLocal.upsertCommandsRecursive(any(), any(), anyInt(), intfac.capture())).thenCallRealMethod();
    ArgumentCaptor<SlashCommandUpsertLocal.GuildCommandReload> intf = ArgumentCaptor.forClass(SlashCommandUpsertLocal.GuildCommandReload.class);
    util.when(() -> SlashCommandUpsertLocal.upsertGuildCommand(any(), intf.capture())).then(i -> {
      intf.getValue().onFinish();
      return null;
    });
    assertDoesNotThrow(() -> SlashCommandUpsertLocal.main(new String[0]));

  }

  @Test
  void start() {
    SlashCommandClient slashCommandClient = new SlashCommandClient(new SlashCommand[]{slashCommandAlternative});
    slashCommandClient.start();
    verify(slashCommandAlternative).onBotStart();
  }

  @Test
  void ready() {
    JDA jda = mock(JDA.class);
    SlashCommandClient slashCommandClient = new SlashCommandClient(new SlashCommand[]{slashCommandAlternative});
    slashCommandClient.ready(jda);
    verify(slashCommandAlternative).onJDAReady(any());
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

  @Mock
  static SlashCommand slashCommandAlternative = new SlashCommand() {

    @Override
    protected void execute(SlashCommandEvent event) {

    }
  };
}