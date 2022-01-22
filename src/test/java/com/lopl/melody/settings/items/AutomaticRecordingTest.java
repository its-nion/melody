package com.lopl.melody.settings.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of {@link AutomaticRecording}
 */
class AutomaticRecordingTest {

  AutomaticRecording automaticRecording;

  @BeforeEach
  void setUp() {
    automaticRecording = new AutomaticRecording();
  }

  @Test
  void getDefaultValue() {
    assertSame(AutomaticRecording.Value.off().getData(), automaticRecording.getDefaultValue().getData());
  }

  @Test
  void getPossibilities() {
    assertEquals(3, automaticRecording.getPossibilities().size());
  }

  @Test
  void updateData() {
    automaticRecording.updateData(1);
    assertSame(1, automaticRecording.getValue().getData());
    automaticRecording.updateData(23);
    assertSame(automaticRecording.getDefaultValue().getData(), automaticRecording.getValue().getData());
  }

  @Test
  void getName() {
    assertEquals("Automatic recording", automaticRecording.getName());
  }

  @Test
  void stateOff(){
    AutomaticRecording.Value value = AutomaticRecording.Value.off();
    automaticRecording.updateData(value.getData());
    assertTrue(automaticRecording.getValue().isOff());
    assertFalse(automaticRecording.getValue().isOn());
    assertEquals(value.getValueRepresentation(), automaticRecording.getValue().getValueRepresentation());
  }

  @Test
  void stateRecord(){
    AutomaticRecording.Value value = AutomaticRecording.Value.record();
    automaticRecording.updateData(value.getData());
    assertFalse(automaticRecording.getValue().isOff());
    assertTrue(automaticRecording.getValue().isOn());
    assertTrue(automaticRecording.getValue().isWithMessage());
    assertEquals(value.getValueRepresentation(), automaticRecording.getValue().getValueRepresentation());
  }

  @Test
  void stateRecordWithoutMessage(){
    AutomaticRecording.Value value = AutomaticRecording.Value.recordNoMessage();
    automaticRecording.updateData(value.getData());
    assertFalse(automaticRecording.getValue().isOff());
    assertTrue(automaticRecording.getValue().isOn());
    assertFalse(automaticRecording.getValue().isWithMessage());
    assertEquals(value.getValueRepresentation(), automaticRecording.getValue().getValueRepresentation());
  }

  @Test
  void stateIllegal(){
    AutomaticRecording.Value value = new AutomaticRecording.Value(0);
    automaticRecording.updateData(value.getData());
    assertSame(automaticRecording.getDefaultValue().getData(), automaticRecording.getValue().getData());

    value = new AutomaticRecording.Value(Integer.MIN_VALUE);
    automaticRecording.updateData(value.getData());
    assertSame(automaticRecording.getDefaultValue().getData(), automaticRecording.getValue().getData());

    value = new AutomaticRecording.Value(Integer.MAX_VALUE);
    automaticRecording.updateData(value.getData());
    assertSame(automaticRecording.getDefaultValue().getData(), automaticRecording.getValue().getData());
  }
}