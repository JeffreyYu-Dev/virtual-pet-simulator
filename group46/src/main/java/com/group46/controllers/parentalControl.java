package com.group46.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group46.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.group46.components.JSON;
import com.group46.components.parentalController;


public class parentalControl implements Initializable {
  private boolean popupShown = false;

  @FXML
  private TextField fromInput;
  @FXML
  private TextField toInput;

  private final parentalController parentalData = parentalController.getInstance();

  @FXML
  private CheckBox timeLimitBox;
  @FXML
  private Label total;
  //@FXML
  //private javafx.scene.control.ListView<String> deadPetList;

  private List<JsonNode> deadPetData = new ArrayList<>();
  @FXML
  private Label avg;

  private Timeline timer;

  @FXML
  private Label name;
  @FXML
  private Label day;
  @FXML
  private Label timePlayed;
  @FXML
  private Label lastPlaye;
  @FXML
  private ScrollPane deadPetList;
  @FXML
  private boolean timeLimitBypassed = false;
  @FXML
  private Button reviveButton;
  private int selectedDeadPetIndex = -1;
  @FXML
  private ImageView imageContainer;

  //=======================================//
  @FXML
  public boolean getEnableTime() {
    return timeLimitBox.isSelected();
  }
//======================================//

  //============================================//
  // takes you bask to the main menu
  @FXML
  private void goBack() throws IOException {

    App.setRoot("mainMenu");
  }
//=============================================//

  public void initialize(URL location, ResourceBundle resource) {
    boolean enable = parentalData.getEnableTimeVal();

    timeLimitBox.setSelected(enable);
    fromInput.setDisable(!enable);
    toInput.setDisable(!enable);

    total.setText("Total play time: ");
    avg.setText("Average time per day: ");

    timeLimitBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
      fromInput.setDisable(!newVal);
      toInput.setDisable(!newVal);
      parentalData.saveEnableTimeVal(newVal);
    });

    fromInput.setText(parentalData.getFromTime());
    toInput.setText(parentalData.getToTime());

    fromInput.textProperty().addListener((obs, oldVal, newVal) -> {
      parentalData.saveTimeRange(newVal, toInput.getText());
    });

    toInput.textProperty().addListener((obs, oldVal, newVal) -> {
      parentalData.saveTimeRange(fromInput.getText(), newVal);
    });


    start();
    populateDeadPetList();
  }

  private void start() {
    timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTime()));
    timer.setCycleCount(Timeline.INDEFINITE);
    timer.play();
  }

  private void updateTime() {
    long totalMillis = parentalData.getTimeSession();
    long totalMinutes = totalMillis / (1000 * 60);
    long hours = totalMinutes / 60;
    long minutes = totalMinutes % 60;

    int days = parentalData.getPlayDayCount();
    long averagePerDay = days > 0 ? totalMinutes / days : 0;

    total.setText("Total play time: " + hours + "h " + minutes + "m");
    avg.setText("Average time per day: " + averagePerDay + "m");

    if (timeLimitBox.isSelected() && !isWithinAllowedTime() && !popupShown && !timeLimitBypassed) {
      popupShown = true;

      Platform.runLater(() -> {
        Stage stage = (Stage) total.getScene().getWindow();
        boolean unlocked = parentalController.showTimeLimitPopup(stage);
        if (unlocked) {
          timeLimitBypassed = true;
        }
        popupShown = false;
      });
    }
  }

  @FXML
  private void handleReset() {
    parentalData.resetAllData();
    parentalData.startSessionTimer();

    total.setText("Total play time: 0h 0m");
    avg.setText("Average time per day: 0m");
  }

  private List<Path> listFiles(String folderPath) throws IOException {
    Path path = Paths.get(folderPath);
    if (!Files.exists(path)) {
      return new ArrayList<>();
    }

    try (var stream = Files.list(path)) {
      return stream.toList();
    }
  }

  private List<Path> getListOfSaves() {
    try {
      return listFiles(Paths.get(JSON.getDatabaseFolder() + "saves/").toString());
    } catch (IOException e) {
      System.out.println("Error listing save files: " + e.getMessage());
    }
    return new ArrayList<>();
  }

  private void showDeadPetInfo(JsonNode saveData) {
    try {
      String petName = saveData.get("save").get("name").asText();
      String lastPlayed = saveData.get("save").get("last_played").asText();
      int playtime = saveData.get("save").get("playtime").asInt();
      int gameDay = saveData.get("day").asInt();
      String savePetType = saveData.get("pet").get("type").asText();
      displayPet(savePetType);
      name.setText(petName);
      day.setText("Day: " + gameDay);
      timePlayed.setText("Playtime: " + playtime + " mins");
      lastPlaye.setText("Last played: " + lastPlayed);
    } catch (Exception e) {
      e.printStackTrace();
      name.setText("Error loading pet");
      day.setText("");
      timePlayed.setText("");
      lastPlaye.setText("");
    }

  }

  private List<Path> getDeadSaves() {
    List<Path> allSaves = getListOfSaves();
    List<Path> deadSaves = new ArrayList<>();

    for (Path save : allSaves) {
      try {
        JsonNode parsed = JSON.parse("saves/" + save.getFileName().toString().replace(".json", ""));
        if (!parsed.get("status_is_alive").asBoolean()) {
          deadSaves.add(save);
        }
      } catch (IOException e) {
        System.out.println("Error reading save file: " + save.getFileName());
      }
    }
    return deadSaves;
  }

  private void populateDeadPetList() {
    List<Path> deadSaves = getDeadSaves();
    deadPetData.clear();
    VBox container = new VBox(10);

    for (int i = 0; i < deadSaves.size(); i++) {
      Path save = deadSaves.get(i);
      try {
        JsonNode saveData = JSON.parse("saves/" + save.getFileName().toString().replace(".json", ""));
        deadPetData.add(saveData);
        String petName = saveData.get("save").get("name").asText();

        Button petButton = new Button(petName);
        int index = i;
        petButton.setOnAction(e -> {
          selectedDeadPetIndex = index;
          showDeadPetInfo(saveData);
        });
        container.getChildren().add(petButton);
      } catch (IOException e) {
        System.out.println("Error parsing save file: " + save.getFileName());
      }
    }

    deadPetList.setContent(container);

    if (!deadPetData.isEmpty()) {
      selectedDeadPetIndex = 0;
      showDeadPetInfo(deadPetData.get(0));
    }
  }


  @FXML
  private void handleRevive() {
    if (selectedDeadPetIndex >= 0 && selectedDeadPetIndex < deadPetData.size()) {
      JsonNode petData = deadPetData.get(selectedDeadPetIndex);

      try {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode updatedPetData = (ObjectNode) petData;
        updatedPetData.put("status_is_alive", true);

        ObjectNode petNode = mapper.createObjectNode();
        petNode.put("name", updatedPetData.get("pet").get("name").asText());
        petNode.put("type", updatedPetData.get("pet").get("type").asText());
        petNode.put("health", 100);
        petNode.put("energy", 100);
        petNode.put("happiness", 100);
        petNode.put("fullness", 100);
        updatedPetData.put("pet", petNode);
        updatedPetData.put("score", updatedPetData.get("score").asInt());
        
        String fileName = petData.get("save").get("name").asText();
        JSON.toJson("saves/" + fileName, updatedPetData);

        populateDeadPetList();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean isWithinAllowedTime() {
    if (fromInput == null || toInput == null) {
      System.out.println("Text fields are not initialized!");
      return true;
    }
    try {
      LocalTime now = LocalTime.now();

      String fromText = fromInput.getText().trim().replace("\u00A0", "");
      String toText = toInput.getText().trim().replace("\u00A0", "");

      if (fromText.isEmpty() || toText.isEmpty()) return true;

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

      if (!fromText.contains(":")) fromText += ":00";
      if (!toText.contains(":")) toText += ":00";

      LocalTime fromTime = LocalTime.parse(fromText, formatter);
      LocalTime toTime = LocalTime.parse(toText, formatter);

      if (fromTime.isBefore(toTime)) {
        return !now.isBefore(fromTime) && !now.isAfter(toTime);
      } else {
        return !now.isBefore(fromTime) || !now.isAfter(toTime);
      }
    } catch (DateTimeParseException e) {
      System.out.println("Invalid time format in inputs: " + e.getParsedString());
      e.printStackTrace();
      return true;
    }
  }

  private void displayPet(String petType) {
    Image petIcon = new Image(getClass().getResourceAsStream("/com/group46/assets/pets/pixelart " + petType + ".gif"));
    imageContainer.setImage(petIcon);
  }
}