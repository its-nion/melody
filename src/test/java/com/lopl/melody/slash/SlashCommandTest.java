package com.lopl.melody.slash;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlashCommandTest {

  @Test
  void registerButton() {
    Button button = Button.primary("test_id", "Test Button");
    slashCommand.registerButton(button);
    SlashCommand sc = SlashCommandClient.getInstance().buttonManager.request(button);
    assertEquals(slashCommand, sc);
  }

  @Test
  void registerDropdown() {
    SelectionMenu selectionMenu = SelectionMenu.create("test_id").build();
    slashCommand.registerDropdown(selectionMenu);
    SlashCommand sc = SlashCommandClient.getInstance().dropdownManager.request(selectionMenu);
    assertEquals(slashCommand, sc);
  }

  SlashCommand slashCommand = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }
  };
}