package com.lopl.melody.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lopl.melody.utils.Logging;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is the accessor to the properties file.
 * The file is managed here.
 * Change this class only if you want to make changes to the general behaviour of the properties.
 * If you want to add or remove a field in the properties look into the {@link PropertiesData} class.
 * If you want to change the way data is handled, look into the {@link JsonPropertiesProvider} class.
 */
public class JsonProperties {

  private static PropertiesData data;

  /*
   * These static calls are executed before the first call of anything else in this class.
   * The properties file is created if needed. Then the file is loaded.
   * Afterwards all necessary data is processed.
   */
  static {
    createSettingsFile();
    loadSettingsFile();
    if (!data.secretPropertiesLocation.isEmpty())
      loadSecretSettingsFile();
  }

  /**
   * This will check if the properties.json file exists.
   * If not a new one is created.
   */
  private static void createSettingsFile() {
    try {
      final File settingsFile = new File("properties.json");
      if (!settingsFile.exists()) {
        if (settingsFile.createNewFile()) {
          Logging.info(JsonProperties.class, null, null, "Created properties file");
          PropertiesData data = PropertiesData.generateNew();
          storeSettingsFile(settingsFile, data);
        } else Logging.error(JsonProperties.class, null, null, "Could not create properties file");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This will generate the json from the passed PropertiesData.
   * The json is then written to the File
   *
   * @param file a existing file
   * @param data the object to create json for the file
   * @throws IOException if the file can not be written
   */
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

  /**
   * This reads the properties file content as json.
   * The json string is the parsed to a PropertiesData object.
   * This object will update itself and write the updated version to the file.
   * The object is then stored in the static data variable.
   */
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

  /**
   * If the loaded PropertiesData object has a valid entry of a {@link PropertiesData#secretPropertiesLocation} field,
   * The location is parsed here to load values for all secret keys.
   */
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

  /**
   * Use this to access all the data of the Properties file.
   * <p>
   * Example:
   * <pre>
   * String botKey = JsonProperties.getProperties().getBotKey();
   * </pre>
   *
   * @return a handler object to access al the properties data
   */
  public static JsonPropertiesProvider getProperties() {
    return new JsonPropertiesProvider(data);
  }

  public static class Writer {

    public void updateData(PropertiesData data) throws IOException {
      final File settingsFile = new File("properties.json");
      JsonProperties.storeSettingsFile(settingsFile, data);
    }

  }
}
