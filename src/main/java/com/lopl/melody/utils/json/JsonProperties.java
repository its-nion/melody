package com.lopl.melody.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lopl.melody.utils.database.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonProperties {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataBase.class);
  private static PropertiesData data;

  static {
    createSettingsFile();
    loadSettingsFile();
    if (!data.secretPropertiesLocation.isEmpty())
      loadSecretSettingsFile();
  }

  private static void createSettingsFile() {
    try {
      final File settingsFile = new File("properties.json");
      if (!settingsFile.exists()) {
        if (settingsFile.createNewFile()) {
          LOGGER.info("Created properties file");
          PropertiesData data = PropertiesData.generateNew();
          storeSettingsFile(settingsFile, data);
        } else LOGGER.info("Could not create properties file");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void storeSettingsFile(File file, PropertiesData data) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String jsonString = gson.toJson(data);
    fileWriter.write(jsonString);
    fileWriter.flush();
//     This code was a attempt to handle json without gson.
//     It failed when it came to pretty printing.
//     Since gson is already in the project this is no longer needed, but kept for a later use if gson is no longer available
/*
    // generating the json object
    JsonObject jsonObject = new JsonObject();
    for (Field field : data.getClass().getFields()){
      try {
        // getting the fields data
        String fieldName = field.getName();
        Object value = field.get(data);

        // parsing to the right classes
        if (value instanceof Number)
          jsonObject.addProperty(fieldName, (Number) value);
        if (value instanceof String)
          jsonObject.addProperty(fieldName, (String) value);
        if (value instanceof Boolean)
          jsonObject.addProperty(fieldName, (Boolean) value);
        if (value instanceof Number[]){
          JsonArray jsonArray = new JsonArray();
          Number[] valueArray = (Number[]) value;
          for (Number valueArrayElement : valueArray)
            jsonArray.add(valueArrayElement);
          jsonObject.add(fieldName, jsonArray);
        }
      } catch (IllegalAccessException iae){
        iae.printStackTrace();
      }
    }
    String jsonString = jsonObject.toString();
    fileWriter.write(jsonString);
    fileWriter.flush();
*/
  }

  private static void loadSettingsFile() {
    try {
      final File settingsFile = new File("properties.json");
      if (!settingsFile.exists()) {
        return;
      }
      FileReader fileReader = new FileReader(settingsFile);
      Gson gson = new Gson();
      PropertiesData data = gson.fromJson(fileReader, PropertiesData.class);
      data = PropertiesData.generateNew(data);
      JsonProperties.data = data;
      storeSettingsFile(settingsFile, data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void loadSecretSettingsFile() {
    try {
      final File settingsFile = new File(data.secretPropertiesLocation);
      if (!settingsFile.exists()) {
        return;
      }
      FileReader fileReader = new FileReader(settingsFile);
      Gson gson = new Gson();
      PropertiesData secretData = gson.fromJson(fileReader, PropertiesData.class);
      PropertiesData fullData = PropertiesData.addSecret(data, secretData);
      JsonProperties.data = fullData;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static PropertiesData getData() {
    return data;
  }

  public static JsonPropertiesProvider getProperties(){
    return new JsonPropertiesProvider(data);
  }
}
