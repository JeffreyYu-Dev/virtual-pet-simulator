package com.group46.components;
// JSON STUFF from jackson

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JSON {

  // object mapper instance we can use
  private static ObjectMapper objectMapper = objectMapperInstance();
  final private static String databaseFolder = "src/main/resources/com/group46/database/";


  // create an object mapper we can add settings here if we need
  private static ObjectMapper objectMapperInstance() {
    return new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true)
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Get the object mapper
   *
   * @return object mapper
   */
  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  /**
   * This will parse json data from file not string
   *
   * @param fileName
   * @return JsonNode
   * @throws IOException
   */
  public static JsonNode parse(String fileName) throws IOException {
    Path filePath = Paths.get(databaseFolder, fileName + ".json");
  
    //   check if the file does exist
    if (!Files.exists(filePath)) {
      throw new FileNotFoundException("File not found: " + fileName);
    }

    return objectMapper.readTree(filePath.toFile());
  }


  /**
   * This converts a ObjectNode tree into json and stores it somewhere
   *
   * @param fileName
   * @param node
   * @throws IOException
   */
  public static void toJson(String fileName, ObjectNode node) throws IOException {
    Path filePath = Paths.get(databaseFolder, fileName + ".json");
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath.toString()), node);
  }

  public static String getDatabaseFolder() {
    return databaseFolder;
  }


}
