package com.group46.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.fxml.FXML;
import com.group46.App;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;
import com.group46.components.settingsManager;
import com.group46.components.JSON;

import com.group46.components.audioPlayer;

public class settings implements Initializable {


  @FXML
  private CheckBox autoSave;

  @FXML
  private Slider volumeSlider;


  private final settingsManager parsedSettings = settingsManager.getInstance();
  private final audioPlayer player = audioPlayer.getInstance();


  @FXML
  private void goBack() throws IOException {
    App.setRoot("mainMenu");
  }


  private void handleVolumeChange(double volume) {
    System.out.println(volume);

    player.getMediaPlayer().setVolume(volume);
  }


  @FXML
  private void saveSettings() {
    ObjectMapper mapper = JSON.getObjectMapper();

    ObjectNode settings = mapper.createObjectNode();

    settings.put("volume", Math.round(volumeSlider.getValue()));
    settings.put("auto_save", autoSave.isSelected());

    volumeSlider.setValue(Math.round(volumeSlider.getValue()));
    autoSave.setSelected(autoSave.isSelected());
    player.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);

    try {
      System.out.println("SAVING SETTINGS");
      JSON.toJson("settings", settings);
      parsedSettings.updateParsedSettings();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  // THIS COMES WITH EVERY CONTROLLER
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    // Set the volume slider to the current volume setting
    volumeSlider.setValue(parsedSettings.getVolumeValue());
    // Set the auto-save checkbox to the current auto-save setting
    autoSave.setSelected(parsedSettings.getAutoSaveValue());


    //    handle real time audio change
    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      handleVolumeChange(newValue.doubleValue() / 100);
    });
  }
}
