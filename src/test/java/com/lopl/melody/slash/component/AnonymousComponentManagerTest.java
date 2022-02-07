package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnonymousComponentManagerTest {

  @Test
  void cache() {
    AnonymousComponentManager anonymousComponentManager = new AnonymousComponentManager(List.of(slashCommand1, slashCommand2, slashCommand3));
    assertTrue(anonymousComponentManager.contains("test_id"));
    assertThrows(RuntimeException.class, () -> new AnonymousComponentManager(List.of(slashCommand1, slashCommand1)));
  }

  @Test
  void request() {
    AnonymousComponentManager anonymousComponentManager = new AnonymousComponentManager(List.of(slashCommand1));
    SlashCommand sc = anonymousComponentManager.request("test_id");
    assertEquals(slashCommand1, sc);
    assertNull(anonymousComponentManager.request(""));
    assertNull(anonymousComponentManager.request(null));
  }

  SlashCommand slashCommand1 = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Nullable
    @Override
    public List<String> allowAnonymousComponentCall() {
      return List.of("test_id");
    }
  };

  SlashCommand slashCommand2 = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Nullable
    @Override
    public List<String> allowAnonymousComponentCall() {
      return List.of("");
    }
  };

  SlashCommand slashCommand3 = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }

    @Nullable
    @Override
    public List<String> allowAnonymousComponentCall() {
      return null;
    }
  };
}