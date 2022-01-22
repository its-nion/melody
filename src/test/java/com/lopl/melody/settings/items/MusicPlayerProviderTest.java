package com.lopl.melody.settings.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MusicPlayerProviderTest {

  MusicPlayerProvider musicPlayerProvider;

  @BeforeEach
  void setUp() {
    musicPlayerProvider = new MusicPlayerProvider();
  }

  @Test
  void getDefaultValue() {
    assertSame(MusicPlayerProvider.Value.spotify().getData(), musicPlayerProvider.getDefaultValue().getData());
  }

  @Test
  void getPossibilities() {
    assertEquals(2, musicPlayerProvider.getPossibilities().size());
  }

  @Test
  void updateData() {
    musicPlayerProvider.updateData(1);
    assertSame(1, musicPlayerProvider.getValue().getData());
    musicPlayerProvider.updateData(23);
    assertSame(musicPlayerProvider.getDefaultValue().getData(), musicPlayerProvider.getValue().getData());
  }

  @Test
  void getName() {
    assertEquals("Music searcher", musicPlayerProvider.getName());
  }

  @Test
  void stateSpotify(){
    MusicPlayerProvider.Value value = MusicPlayerProvider.Value.spotify();
    musicPlayerProvider.updateData(value.getData());
    assertTrue(musicPlayerProvider.getValue().isSpotify());
    assertFalse(musicPlayerProvider.getValue().isYoutube());
    assertEquals(value.getValueRepresentation(), musicPlayerProvider.getValue().getValueRepresentation());
  }

  @Test
  void stateYoutube(){
    MusicPlayerProvider.Value value = MusicPlayerProvider.Value.youtube();
    musicPlayerProvider.updateData(value.getData());
    assertFalse(musicPlayerProvider.getValue().isSpotify());
    assertTrue(musicPlayerProvider.getValue().isYoutube());
    assertEquals(value.getValueRepresentation(), musicPlayerProvider.getValue().getValueRepresentation());
  }

  @Test
  void stateIllegal(){
    MusicPlayerProvider.Value value = new MusicPlayerProvider.Value(0);
    musicPlayerProvider.updateData(value.getData());
    assertSame(musicPlayerProvider.getDefaultValue().getData(), musicPlayerProvider.getValue().getData());

    value = new MusicPlayerProvider.Value(Integer.MIN_VALUE);
    musicPlayerProvider.updateData(value.getData());
    assertSame(musicPlayerProvider.getDefaultValue().getData(), musicPlayerProvider.getValue().getData());

    value = new MusicPlayerProvider.Value(Integer.MAX_VALUE);
    musicPlayerProvider.updateData(value.getData());
    assertSame(musicPlayerProvider.getDefaultValue().getData(), musicPlayerProvider.getValue().getData());
  }

}