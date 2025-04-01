package com.group46;


import com.group46.controllers.play;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import com.group46.controllers.play;
import com.group46.components.parentalController;

import java.io.IOException;

// GET THE LOAD SETTINGS FUNCTION
import com.group46.components.settingsManager;

import com.group46.components.audioPlayer;

import com.group46.controllers.play;


/**
 * JavaFX App
 */
public class App extends Application {

  private static Scene scene;
  private static String currentScene;

  private audioPlayer player = audioPlayer.getInstance();
  private settingsManager parsedSettings = settingsManager.getInstance();

  // this function loads the initial things

  @Override
  public void start(Stage stage) throws IOException {

    Font.loadFont(getClass().getResourceAsStream("/com/group46/font/pressStart.ttf"), 12);

    // YOU CAN CHANGE INITIAL WINDOW SIZE HERE
    scene = new Scene(loadFXML("mainMenu"), 1280, 720);
    currentScene = "mainMenu";
    stage.setScene(scene);
    stage.show();


    // handle keybinds
    scene.setOnKeyPressed(this::handleKeyPress);

    //  play sound track based on settings
    player.setMedia("music/ariaMath");
    player.getMediaPlayer().setVolume(parsedSettings.getVolumeValue());
    player.getMediaPlayer().setAutoPlay(true);
    player.getMediaPlayer().play();

    parentalController.getInstance().startSessionTimer();
    parentalController.getInstance().getDate();
    stage.setOnCloseRequest(e -> parentalController.getInstance().saveSessionOnExit());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> parentalController.getInstance().saveSessionOnExit()));

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      parentalController.getInstance().saveSessionOnExit();
    }));

  }

  public static Scene getScene() {
    return scene;
  }


  // SET ROOT NODE (this function allows you to switch between scenes)
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFXML(fxml));
    currentScene = fxml;
  }

  // load fxml file
  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/group46/" + fxml + ".fxml"));
    return fxmlLoader.load();
  }

  /**
   * This method listen to keys press and invoke functions based on the combination of keys
   * This is accessible anywhere in game
   *
   * @param keyEvent
   */
  private void handleKeyPress(KeyEvent keyEvent) {
    scene.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.M) {
        player.getMediaPlayer().setMute(!player.getMediaPlayer().isMute());
        event.consume();
      }
    });
  }

  // actually start the app
  public static void main(String[] args) {
    launch();
  }

}