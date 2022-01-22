package com.lopl.melody.settings.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link DefaultMusicType}
 */
class DefaultMusicTypeTest {

  DefaultMusicType defaultMusicType;

  @BeforeEach
  void setUp() {
    defaultMusicType = new DefaultMusicType();
  }

  @Test
  void getDefaultValue() {
    assertSame(DefaultMusicType.Value.track().getData(), defaultMusicType.getDefaultValue().getData());
  }

  @Test
  void getPossibilities() {
    assertEquals(3, defaultMusicType.getPossibilities().size());
  }

  @Test
  void updateData() {
    defaultMusicType.updateData(1);
    assertSame(1, defaultMusicType.getValue().getData());
    defaultMusicType.updateData(23);
    assertSame(defaultMusicType.getDefaultValue().getData(), defaultMusicType.getValue().getData());
  }

  @Test
  void getName() {
    assertEquals("Default music type", defaultMusicType.getName());
  }

  @Test
  void stateTrack() {
    DefaultMusicType.Value value = DefaultMusicType.Value.track();
    defaultMusicType.updateData(value.getData());
    assertTrue(defaultMusicType.getValue().isTrack());
    assertFalse(defaultMusicType.getValue().isPlaylist());
    assertFalse(defaultMusicType.getValue().isUser());
    assertEquals(value.getValueRepresentation(), defaultMusicType.getValue().getValueRepresentation());
  }

  @Test
  void statePlaylist() {
    DefaultMusicType.Value value = DefaultMusicType.Value.playlist();
    defaultMusicType.updateData(value.getData());
    assertFalse(defaultMusicType.getValue().isTrack());
    assertTrue(defaultMusicType.getValue().isPlaylist());
    assertFalse(defaultMusicType.getValue().isUser());
    assertEquals(value.getValueRepresentation(), defaultMusicType.getValue().getValueRepresentation());
  }

  @Test
  void stateUser() {
    //TODO provide user feature

  }

  @Test
  void stateIllegal() {
    DefaultMusicType.Value value = new DefaultMusicType.Value(0);
    defaultMusicType.updateData(value.getData());
    assertSame(defaultMusicType.getDefaultValue().getData(), defaultMusicType.getValue().getData());

    value = new DefaultMusicType.Value(Integer.MIN_VALUE);
    defaultMusicType.updateData(value.getData());
    assertSame(defaultMusicType.getDefaultValue().getData(), defaultMusicType.getValue().getData());

    value = new DefaultMusicType.Value(Integer.MAX_VALUE);
    defaultMusicType.updateData(value.getData());
    assertSame(defaultMusicType.getDefaultValue().getData(), defaultMusicType.getValue().getData());
  }

}