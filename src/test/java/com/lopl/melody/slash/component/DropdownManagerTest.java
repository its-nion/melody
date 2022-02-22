package com.lopl.melody.slash.component;

import com.lopl.melody.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DropdownManagerTest {

  @Test
  void cache() {
    DropdownManager dropdownManager = new DropdownManager();
    SelectionMenu selectionMenu = SelectionMenu.create("test_id").build();
    dropdownManager.cache(selectionMenu, slashCommand);
    assertEquals(slashCommand, dropdownManager.request(selectionMenu));
    dropdownManager.cache(selectionMenu, slashCommand);
    assertEquals(slashCommand, dropdownManager.request(selectionMenu));
    dropdownManager.cache(null, slashCommand);
    assertEquals(slashCommand, dropdownManager.request(selectionMenu));
    dropdownManager.cache(selectionMenu, null);
    assertEquals(slashCommand, dropdownManager.request(selectionMenu));
    dropdownManager.cache(null, null);
    assertEquals(slashCommand, dropdownManager.request(selectionMenu));
  }

  @Test
  void request() {
    DropdownManager dropdownManager = new DropdownManager();
    SelectionMenu selectionMenu = SelectionMenu.create("test_id").build();
    SelectionMenu selectionMenu2 = SelectionMenu.create("test_id_1").build();
    dropdownManager.cache(selectionMenu, slashCommand);
    assertEquals(slashCommand, dropdownManager.request(selectionMenu));
    assertNull(dropdownManager.request(null));
    assertNull(dropdownManager.request(selectionMenu2));
  }

  SlashCommand slashCommand = new SlashCommand() {
    @Override
    protected void execute(SlashCommandEvent event) {

    }
  };
}