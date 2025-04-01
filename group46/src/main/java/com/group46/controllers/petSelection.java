package com.group46.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.group46.App;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


/**
 * @author Jagger Adams
 */
public class petSelection implements Initializable {

  /**
   * Declares instance variables for FXML buttons, images, divs, etc.
   */
  public static String selectedPet = "";
  @FXML
  private ImageView image1;
  @FXML
  private ImageView image2;
  @FXML
  private ImageView image3;
  @FXML
  private HBox imageContainer;
  @FXML
  private Button nextButton;


  /**
   * This method is called when any of the 3 pet images is selected.
   * It automatically deselects all other pet options, ensuring only one is chosen.
   * It adds a css class to the selected image so gets a blue background and border.
   * It also only enables the 'next' button once a pet is selected.
   * It saves the chosen animal to a public string
   * @param event
   */
  @FXML
  private void handleImageClick(MouseEvent event) {
    ImageView clickedImage = (ImageView) event.getSource();
    StackPane clickedPane = (StackPane) clickedImage.getParent();

    imageContainer.getChildren().forEach(node -> {
      if (node instanceof StackPane) {
        node.getStyleClass().remove("selected-image");
      }
    });

    clickedPane.getStyleClass().add("selected-image");


    if (clickedImage == image1) {
      selectedPet = "dog";
    } else if (clickedImage == image2) {
      selectedPet = "cat";
    } else if (clickedImage == image3) {
      selectedPet = "fish";
    }
    System.out.println(selectedPet);
    nextButton.setDisable(false);
  }


  /**
   * This method sets the scene back to main menu when the "back" button is clicked
   * @param event
   * @throws IOException
   */
  @FXML
  private void goBack(ActionEvent event) throws IOException {
    App.setRoot("mainMenu");
  }

  /**
   * This method sets the scene to the name selection scene when the "next" button is clicked
   * @param event
   * @throws IOException
   */
  @FXML
  private void goNext(ActionEvent event) throws IOException {
    App.setRoot("nameSelection");
  }


  // THIS COMES WITH EVERY CONTROLLER

  /**
   * This method just sends image paths to the FXML file to display the
   * 3 pet options
   * @param url
   * The location used to resolve relative paths for the root object
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    try {
      // Load dog image
      Image dogImage = new Image(getClass().getResourceAsStream("/com/group46/assets/pets/pixelart dog.gif"));
      image1.setImage(dogImage);

      // Load cat image
      Image catImage = new Image(getClass().getResourceAsStream("/com/group46/assets/pets/pixelart cat.gif"));
      image2.setImage(catImage);

      // Load fish image
      Image fishImage = new Image(getClass().getResourceAsStream("/com/group46/assets/pets/pixelart fish.gif"));
      image3.setImage(fishImage);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to load one or more images: " + e.getMessage());
    }
  }
}
