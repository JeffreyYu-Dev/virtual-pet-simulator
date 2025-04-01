package com.group46.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.fxml.FXML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class settingsManager {

  private JsonNode parsedSettings;

  private static settingsManager instance;

  private settingsManager() {
    loadSettings();
  }

  public static settingsManager getInstance() {
    if (instance == null) {
      instance = new settingsManager();
    }

    return instance;
  }


  public int getVolumeValue() {
    return this.parsedSettings.get("volume").intValue();
  }

  public boolean getAutoSaveValue() {
    return this.parsedSettings.get("auto_save").booleanValue();
  }

  public void updateParsedSettings() {
    try {
      this.parsedSettings = JSON.parse("settings");
    } catch (IOException e) {
      System.out.println("Error in parsing settings in updateParsedSettings.");
      throw new RuntimeException(e);
    }
  }


  @FXML
private void loadSettings() {
    Path filePath = Paths.get(JSON.getDatabaseFolder(), "settings.json");
    boolean createNewSettings = false;

    // Check if file exists
    if (!Files.exists(filePath)) {
        createNewSettings = true;
    } else {
        // File exists, but check if it's valid JSON
        try {
            this.parsedSettings = JSON.parse("settings");
        } catch (IOException e) {
            System.out.println("Error reading settings file: " + e.getMessage());
            // If parsing fails, consider the file corrupted and recreate it
            createNewSettings = true;
        }
    }

    // Create default settings if needed
    if (createNewSettings) {
        ObjectMapper mapper = JSON.getObjectMapper();
        ObjectNode settings = mapper.createObjectNode();
        
        settings.put("volume", 50);
        settings.put("auto_save", true);

        try {
            // Ensure directory exists
            Files.createDirectories(filePath.getParent());
            // Write default settings
            JSON.toJson("settings", settings);
            // Now parse the newly created settings
            this.parsedSettings = JSON.parse("settings");
        } catch (IOException e) {
            System.err.println("Failed to create default settings: " + e.getMessage());
            throw new RuntimeException("Could not initialize application settings", e);
        }
    }
}
}
