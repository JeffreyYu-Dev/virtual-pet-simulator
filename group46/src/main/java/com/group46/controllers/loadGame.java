package com.group46.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.group46.components.JSON;
import javafx.fxml.FXML;
import com.group46.App;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.stream.Stream;


public class loadGame implements Initializable {


  static JsonNode selectedSave;

  @FXML
  private VBox loadSavesContainer;

  @FXML
  private Button nextButton;

  private List<Path> listOfSaves;

  @FXML
  private VBox deleteButtonContainer;


  private final Button deleteButton = new Button();

  private List<Path> listFiles(String directory) throws IOException {
    List<Path> fileList;
    try (Stream<Path> paths = Files.list(Paths.get(directory))) {
      fileList = paths
          .filter(Files::isRegularFile)  // Only files, no directories
          .toList();
    }
    return fileList;
  }


  @FXML
  private void goBack() throws IOException {
    App.setRoot("mainMenu");
    selectedSave = null;
    nextButton.setDisable(true);
    deleteButton.setDisable(true);

  }


  private String calculateFormattedTime(int playtime) {
    int hours = playtime / 3600;
    int minutes = playtime % 3600 / 60;

    if (hours < 1) {
      return String.format("%02d minute", minutes);
    }

    if (minutes == 0) {
      return String.format("%02d hour" + (hours != 1 ? "s" : ""), hours);
    }

    return String.format("%02d hours %02d minutes", hours, minutes);
  }

  @FXML
  private HBox gameSaveUI(JsonNode parsedSaveFile) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    String pet = parsedSaveFile.get("pet").get("type").asText();
    String saveName = parsedSaveFile.get("save").get("name").asText();
    int daysAlive = parsedSaveFile.get("day").asInt();
    boolean isAlive = parsedSaveFile.get("status_is_alive").asBoolean();
    LocalDateTime lastPlayed = LocalDateTime.parse(parsedSaveFile.get("save").get("last_played").asText(), formatter);
    int year = lastPlayed.getYear();
    int month = lastPlayed.getMonthValue();
    int day = lastPlayed.getDayOfMonth();

    int playtime = parsedSaveFile.get("save").get("playtime").asInt();
    int score = parsedSaveFile.get("score").asInt();


    String subheaderStyles = "-fx-font-size: 1.5em; -fx-font-weight: 700";
    String headerStyles = "-fx-font-size: 3em; -fx-font-weight: 900";


    HBox container = new HBox();
    container.getStyleClass().add("gameSave");
    container.setPadding(new Insets(20, 20, 20, 20));


    if (!isAlive) {
      container.getStyleClass().add("dead");
    } else {
      container.getStyleClass().add("default");
    }


    Label saveNameLabel = new Label(saveName);
    saveNameLabel.setStyle(headerStyles);
    VBox.setVgrow(saveNameLabel, Priority.ALWAYS);

    Label scoreLabel = new Label("Score: " + score);
    scoreLabel.setStyle(subheaderStyles);

    Label lastPlayedLabel = new Label("Last Played: " + year + "-" + month + "-" + day);
    lastPlayedLabel.setStyle(subheaderStyles);

    Label playtimeLabel = new Label("Playtime: " + calculateFormattedTime(playtime));
    playtimeLabel.setStyle(subheaderStyles);

    Label statusLabel = new Label("Status: " + (isAlive ? "Alive" : "Dead"));
    statusLabel.setStyle(subheaderStyles);

    Label dayLabel = new Label("Day: " + daysAlive);
    dayLabel.setStyle(subheaderStyles);

    HBox leftSideDetailsContainer = new HBox();
    leftSideDetailsContainer.setAlignment(Pos.CENTER);
    leftSideDetailsContainer.setPadding(new Insets(10, 10, 10, 10));


    VBox leftSideDetails = new VBox();
    leftSideDetails.setAlignment(Pos.CENTER_LEFT);
    leftSideDetails.setStyle("-fx-padding: 0 0 0 20");

    HBox leftSideSmallDetails = new HBox();
    leftSideSmallDetails.setAlignment(Pos.CENTER);
    leftSideSmallDetails.setSpacing(40);
    leftSideSmallDetails.setStyle("-fx-padding: 50 0 0 0");

    leftSideSmallDetails.getChildren().add(statusLabel);
    leftSideSmallDetails.getChildren().add(dayLabel);


    leftSideDetails.getChildren().add(saveNameLabel);
    leftSideDetails.getChildren().add(leftSideSmallDetails);


//    pet icon
    Image petIcon;
    try {

      petIcon = new Image(getClass().getResourceAsStream("/com/group46/assets/pets/pixelart " + pet + ".gif"));
    } catch (Exception e) {
      petIcon = new Image(getClass().getResourceAsStream("/com/group46/assets/images/noImageAvailable.png"));
    }


    ImageView imageView = new ImageView(petIcon);
    imageView.setFitHeight(230);
    imageView.setFitWidth(230);
    imageView.setPreserveRatio(true);

    leftSideDetailsContainer.getChildren().add(imageView);
    leftSideDetailsContainer.getChildren().add(leftSideDetails);


    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);


    VBox rightSideDetailsContainer = new VBox();
    rightSideDetailsContainer.setAlignment(Pos.CENTER_LEFT);
    rightSideDetailsContainer.setSpacing(20);
    rightSideDetailsContainer.setStyle("-fx-padding: 0 20 0 0");

    rightSideDetailsContainer.getChildren().add(scoreLabel);
    rightSideDetailsContainer.getChildren().add(playtimeLabel);
    rightSideDetailsContainer.getChildren().add(lastPlayedLabel);


    container.getChildren().add(leftSideDetailsContainer);
    container.getChildren().add(spacer);
    container.getChildren().add(rightSideDetailsContainer);

    container.setId(saveName);

    // Add a method to handle selection styling
    container.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
      selectedSave(container, parsedSaveFile);
    });

    return container;
  }


  //  claude the goat
  private void selectedSave(HBox container, JsonNode parsedSaveFile) {
    // Iterate through all children in the loadSavesContainer
    for (Node node : loadSavesContainer.getChildren()) {
      if (node instanceof HBox saveContainer && node.getStyleClass().contains("gameSave")) {
        // Remove 'selected' class from all containers
        saveContainer.getStyleClass().remove("selected");

        // Restore original styling
        if (saveContainer.getStyleClass().contains("dead")) {
          saveContainer.getStyleClass().remove("selected");
        } else {
          if (!saveContainer.getStyleClass().contains("default")) {
            saveContainer.getStyleClass().add("default");
          }
        }
      }
    }

    // Remove default from the selected container and add selected
    container.getStyleClass().remove("default");
    container.getStyleClass().add("selected");

    // Store the selected save file
    selectedSave = parsedSaveFile;

    if (!selectedSave.get("status_is_alive").asBoolean()) {
      nextButton.setDisable(true);
      deleteButton.setDisable(true);

    } else {
      nextButton.setDisable(false);
      deleteButton.setDisable(false);
    }

  }


  public static JsonNode getSelectedSave() {
    return selectedSave;
  }


  @FXML
  public void deleteSave() {
//    delete json and delete ui

    if (!nextButton.isDisabled()) {
      try {
        String dir = "src/main/resources/com/group46/database/saves";
        Path saveFileToDelete = Paths.get(dir, selectedSave.get("save").get("name").asText() + ".json");
        Files.delete(saveFileToDelete);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }

      // delete ui
      loadSavesContainer.getChildren().removeIf(save ->
          save.getId().equals(selectedSave.get("save").get("name").asText())
      );


      if (listOfSaves.isEmpty()) {
        selectedSave = null;
        deleteButton.setDisable(true);
        nextButton.setDisable(true);
      }

      loadGameSaves();
    }
  }

  private List<Path> sort(List<Path> saves) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    return saves.stream()
        .sorted((path1, path2) -> {
          try {
            // Parse JSON for both saves
            JsonNode json1 = JSON.parse("saves/" + path1.getFileName().toString().replace(".json", ""));
            JsonNode json2 = JSON.parse("saves/" + path2.getFileName().toString().replace(".json", ""));

            // Get status (alive/dead) for both saves
            boolean isAlive1 = json1.get("status_is_alive").asBoolean();
            boolean isAlive2 = json2.get("status_is_alive").asBoolean();

            // If alive status differs, alive pets come first
            if (isAlive1 != isAlive2) {
              return isAlive1 ? -1 : 1;
            }

            // If both have same alive status, sort by last_played (most recent first)
            LocalDateTime save1 = LocalDateTime.parse(
                json1.get("save").get("last_played").asText(),
                formatter
            );

            LocalDateTime save2 = LocalDateTime.parse(
                json2.get("save").get("last_played").asText(),
                formatter
            );

            return save2.compareTo(save1);
          } catch (Exception e) {
            // Handle any reading or parsing errors
            e.printStackTrace();
            return 0;
          }
        }).toList();
  }


  private void displaySaves(List<Path> saves) {
    List<Path> sortedSaves = sort(saves);
//    for each save create and add to container
    sortedSaves.forEach(save -> {
      try {
        JsonNode parsedSaveFile = JSON.parse("saves/" + save.getFileName().toString().replace(".json", ""));
        System.out.println(parsedSaveFile.get("save").get("name").asText());
        loadSavesContainer.getChildren().add(gameSaveUI(parsedSaveFile));
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    });
  }

  @FXML
  private void next() throws IOException {
    App.setRoot("play");
    selectedSave = null;
    nextButton.setDisable(true);
    deleteButton.setDisable(true);

  }

  private List<Path> getListOfSaves() {
    try {
      return listFiles(Paths.get(JSON.getDatabaseFolder() + "saves/").toString());
    } catch (IOException e) {
      System.out.println(e);
    }
    return null;
  }

  /**
   * THIS WILL LOAD GAME UI
   */
  private void loadGameSaves() {
    loadSavesContainer.getChildren().clear();
    listOfSaves = getListOfSaves();
    if (listOfSaves != null) {
      displaySaves(listOfSaves);
    }
  }

  @FXML
  private void deleteButtonUI() {
    //    create delete button
    Image trashImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/trash.png"));
    ImageView trashIcon = new ImageView(trashImage);
    trashIcon.setFitWidth(30);
    trashIcon.setFitHeight(30);
    trashIcon.setPreserveRatio(true);
    deleteButton.setGraphic(trashIcon);
    deleteButton.getStyleClass().add("deleteButton");
    deleteButton.setOnAction(event -> {
      deleteSave();
    });

    deleteButton.setDisable(true);
    deleteButtonContainer.getChildren().add(deleteButton);
  }


  // THIS COMES WITH EVERY CONTROLLER
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    loadGameSaves();
    deleteButtonUI();

    nextButton.setDisable(true);
    deleteButton.setDisable(true);
  }
}
