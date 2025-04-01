package com.group46.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group46.App;
import com.group46.components.JSON;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author Jagger Adams
 */


public class nameSelection implements Initializable {
  @FXML
  private ImageView imageView;

  @FXML
  private Button createButton;

  @FXML
  private TextField petNameTextField;

  @FXML
  private VBox nameYourPetContainer;

  private String animal = petSelection.selectedPet;
  public static String petName;


  /**
   * This method just makes the create button disabled until there is at least one
   * character in the name textfield.
   * Also calls helper method to ensure correct pet is displayed on naming screen
   *
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (animal != null) {
      updatePetImage();
    }
    createButton.disableProperty().bind(
        petNameTextField.textProperty().isEmpty()
    );

    petNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!oldValue.equals(newValue)) {
        clearFileExistPopUp();
      }
    });

  }

  /**
   * This method selects which pet to display on the naming scene
   * based on which pet was selected on the previous scene (pet selection scene).
   */
  private void updatePetImage() {
    if (animal == null || imageView == null) return;

    String imagePath;

    switch (animal) {
      case "dog":
        imagePath = "/com/group46/assets/pets/pixelart dog.gif";
        break;
      case "cat":
        imagePath = "/com/group46/assets/pets/pixelart cat.gif";
        break;
      case "fish":
        imagePath = "/com/group46/assets/pets/pixelart fish.gif";
        break;
      default:
        imagePath = "/dog.gif";
    }

    try {
      URL imageUrl = getClass().getResource(imagePath);

      if (imageUrl == null) {
        System.err.println("Image not found: " + imagePath);
        return;
      }

      Image image = new Image(imageUrl.openStream());
      imageView.setImage(image);

    } catch (Exception e) {
      System.err.println("Error loading image: " + imagePath);
      e.printStackTrace();
    }
  }

  /**
   * This method changes the scene back to pet selection when the user clicks the
   * 'back' button
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void goBack(ActionEvent event) throws IOException {
    App.setRoot("petSelection");
  }


  private void clearFileExistPopUp() {
    nameYourPetContainer.getChildren().clear();
    petNameTextField.setStyle("-fx-text-fill: black;");
    
  }

  //
  private void fileExistPopUp() {


    petNameTextField.setStyle("-fx-text-fill: orange;");

    Label fileExist = new Label();
    fileExist.setText("Save: " + petName + " Already Exists!");
    fileExist.setStyle("-fx-text-fill: orange;");

    nameYourPetContainer.getChildren().add(fileExist);
  }


  /**
   * This method saves the entered pet info to a JSON node
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void createPet() throws IOException {
    petName = petNameTextField.getText();
    System.out.println(petName);

//    check if the file exist before adding the game
    if (Files.exists(Paths.get(JSON.getDatabaseFolder() + "saves/" + petName + ".json"))) {
      fileExistPopUp();
      System.out.println("Save with " + petName + " already exists");
      return;
    }

    //creating nodes
    ObjectMapper mapper = JSON.getObjectMapper();
    //save node
    ObjectNode save = mapper.createObjectNode();
    save.put("name", petName);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime currentDate = LocalDateTime.now();
    String formattedDate = currentDate.format(formatter);

    save.put("last_played", formattedDate);
    save.put("playtime", 0);
    //pet node
    ObjectNode pet = mapper.createObjectNode();
    pet.put("name", petName);
    pet.put("type", animal);
    pet.put("health", 100);
    pet.put("energy", 100);
    pet.put("happiness", 100);
    pet.put("fullness", 100);
    //saving sub nodes to container node
    ObjectNode container = mapper.createObjectNode();
    container.put("save", save);
    container.put("pet", pet);
    container.put("day", 0);
    container.put("status_is_alive", true);
    container.put("score", 0);


    try {
      JSON.toJson("saves/" + petName, container);
    } catch (IOException e) {
      System.err.println("Error creating pet: " + e.getMessage());
    }


    App.setRoot("play");
  }

  public static JsonNode getSave() {
    try {
      return JSON.parse("saves/" + petName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}