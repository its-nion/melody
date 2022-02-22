package com.lopl.melody.slash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlashCommandsTest {

  @Test
  void getCommands() {
    assertNotNull(SlashCommands.getCommands());
    assertNotEquals(0, SlashCommands.getCommands().size());
  }
}