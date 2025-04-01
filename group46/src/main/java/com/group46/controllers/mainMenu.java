package com.group46.controllers;

import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.group46.App;
import com.group46.components.parentalController;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


// functions for the main menu happen in here
public class mainMenu implements Initializable {

  @FXML
  private Button loadButton;

  @FXML
  private Button newButton;

  @FXML
  private ImageView menuGif;

  private parentalController parentController = parentalController.getInstance();

  /**
   * This method changes the scene to the pet selection scene
   * when the user clicks the 'new' button on the main menu
   *
   * @throws IOException
   */
  @FXML
  private void newGame() throws IOException {
    System.out.println("new game");
    App.setRoot("petSelection");
  }

  /**
   * This method changes the scene to the load game scene
   * when the user clicks the 'load' button on the main menu
   *
   * @throws IOException
   */
  @FXML
  private void loadGame() throws IOException {
    System.out.println("load game");
    App.setRoot("loadGame");
  }

  /**
   * This method changes the scene to the tutorial scene
   * when the user clicks the 'tutorial' button on the main menu
   *
   * @throws IOException
   */
  @FXML
  private void tutorial() throws IOException {
    System.out.println("tutorial");
    App.setRoot("tutorial");
  }

  /**
   * This method calls a pop-up window to prompt the user for a password
   * they click the 'parental controls' button on the main menu
   *
   * @throws IOException
   */
  @FXML
  private void parentalControls() throws IOException {
    System.out.println("parental controls");
    parentalController.showPasswordPrompt();
  }

  /**
   * This method changes the scene to the settings scene
   * when the user clicks the 'settings' button on the main menu
   *
   * @throws IOException
   */
  @FXML
  private void settings() throws IOException {
    System.out.println("settings");
    App.setRoot("settings");
  }

  /**
   * This method terminates the program
   * when the user clicks the 'quit' button on the main menu
   */
  @FXML
  private void quit() {
    System.out.println("quit");
    System.exit(0);
  }


  //    THIS COMES WITH EVERY NEW CONTROLLER

  /**
   * This method sends an image path to the FXML image view so it can display the menu gif
   *
   * @param url and resource bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Image i = new Image(getClass().getResource("/com/group46/assets/images/menuGif.gif").toString());
    menuGif.setImage(i);

  }
}
