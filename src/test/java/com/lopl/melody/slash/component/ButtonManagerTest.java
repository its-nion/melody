package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ButtonManagerTest {

  @Test
  void cache() {
    ButtonManager buttonManager = new ButtonManager();
    Button button = Button.primary("test_id", "Test Button");
    buttonManager.cache(button, slashCommand);
    assertEquals(slashCommand, buttonManager.request(button));
    buttonManager.cache(button, slashCommand);
    assertEquals(slashCommand, buttonManager.request(button));
    buttonManager.cache(null, slashCommand);
    assertEquals(slashCommand, buttonManager.request(button));
    buttonManager.cache(button, null);
    assertEquals(slashCommand, buttonManager.request(button));
    buttonManager.cache(null, null);
    assertEquals(slashCommand, buttonManager.request(button));
  }

  @Test
  void request() {
    ButtonManager buttonManager = new ButtonManager();
    Button button = Button.primary("test_id", "Test Button");
    Button button2 = Button.secondary("test_id_2", "Test Button");
    buttonManager.cache(button, slashCommand);
    assertEquals(slashCommand, buttonManager.request(button));
    assertNull(buttonManager.request(null));
    assertNull(buttonManager.request(button2));
  }

  SlashCommand slashCommand = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }
  };
}