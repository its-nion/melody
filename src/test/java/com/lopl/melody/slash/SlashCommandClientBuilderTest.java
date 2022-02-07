package com.lopl.melody.slash;

import com.lopl.melody.Token;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlashCommandClientBuilderTest {

  @Test
  void addCommand() {
    SlashCommandClientBuilder sccb = new SlashCommandClientBuilder();
    sccb.addCommand(slashCommand);
    assertNotNull(sccb.commands);
    assertNotEquals(sccb.commands.size(), 0);
    assertTrue(sccb.commands.contains(slashCommand));
  }

  @Test
  void addCommands() {
    SlashCommandClientBuilder sccb = new SlashCommandClientBuilder();
    sccb.addCommands(slashCommand, slashCommandAlternative, slashCommand);
    assertNotNull(sccb.commands);
    assertNotEquals(0, sccb.commands.size());
    assertTrue(sccb.commands.contains(slashCommand));
  }

  @Test
  void addEventListener() {
    SlashCommandClientBuilder sccb = new SlashCommandClientBuilder();
    sccb.addEventListener(JDABuilder.createLight(Token.BOT_TOKEN), slashCommand);
    assertNotNull(sccb.eventListenerCommands);
    assertNotEquals(0, sccb.eventListenerCommands.size());
    assertTrue(sccb.eventListenerCommands.contains(slashCommand));
  }

  @Test
  void addEventListeners() {
    SlashCommandClientBuilder sccb = new SlashCommandClientBuilder();
    sccb.addEventListeners(JDABuilder.createLight(Token.BOT_TOKEN), slashCommand, slashCommandAlternative, slashCommand);
    assertNotNull(sccb.eventListenerCommands);
    assertNotEquals(0, sccb.eventListenerCommands.size());
    assertTrue(sccb.eventListenerCommands.contains(slashCommand));
    assertFalse(sccb.eventListenerCommands.contains(slashCommandAlternative));
  }

  @Test
  void build() {
    SlashCommandClientBuilder sccb = new SlashCommandClientBuilder();
    sccb.addCommand(slashCommand);
    sccb.addEventListener(JDABuilder.createLight(Token.BOT_TOKEN), slashCommand);
    SlashCommandClient scc = sccb.build();
    assertNotNull(scc);
    assertNotNull(scc.slashCommands);
    assertEquals(scc.slashCommands.length, sccb.commands.size());
  }

  static SlashCommand slashCommand = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Nullable
    @Override
    public ListenerAdapter getCommandEventListener() {
      return new ListenerAdapter() {

      };
    }
  };

  static SlashCommand slashCommandAlternative = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }
  };
}