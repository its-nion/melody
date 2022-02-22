package com.lopl.melody.utils.json;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonPropertiesTest {

  @Test
  void testFileCreation(){
    PropertiesData data = JsonProperties.getData();
    File file = new File("properties.json");
    assertTrue(file.exists());
    assertNotNull(data);
  }

  @Test
  void testBotKey(){
    String botKey = JsonProperties.getProperties().getBotKey();
    assertNotNull(botKey);
  }

}