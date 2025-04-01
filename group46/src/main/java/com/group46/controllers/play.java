package com.group46.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group46.App;
import com.group46.components.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.group46.controllers.DailyOpening.generateGift;


/**
 * @author Jagger Adams
 */
public class play implements Initializable {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private JsonNode selectedSave;

  private pet pet;

  private long startTime;

  private int day;

  private LocalDateTime lastPlayed;

  private Timeline statsTimer = new Timeline();

  private Timeline sleepTimer = new Timeline();

  private Timeline gameLoopTimer = new Timeline();

  private Timeline autoSaveTimer = new Timeline();

  private boolean autoSaveValue;

  private final settingsManager settingsInstance = settingsManager.getInstance();

  private boolean dogPenalty = false;

  private boolean catPenalty = false;

  private boolean fishPenalty = false;

  private final Inventory inventory = Inventory.getInventory();

  @FXML
  private Button bedButtonId;

  @FXML
  private Button playButtonId;

  @FXML
  private Button exerciseButtonId;

  @FXML
  private Button vetButtonId;

  @FXML
  private Button commonFoodButtonId;

  @FXML
  private Button rareFoodButtonId;

  @FXML
  private Button legendaryFoodButtonId;

  @FXML
  private Button commonGiftButtonId;

  @FXML
  private Button rareGiftButtonId;

  @FXML
  private Button legendaryGiftButtonId;

  @FXML
  private BorderPane container;

  @FXML
  private ImageView spriteBox;

  @FXML
  private ProgressBar healthBar;

  @FXML
  private ProgressBar happinessBar;

  @FXML
  private ProgressBar energyBar;

  @FXML
  private ProgressBar fullnessBar;

  @FXML
  private Label commonFood;

  @FXML
  private Label rareFood;

  @FXML
  private Label legendaryFood;

  @FXML
  private Label commonGift;

  @FXML
  private Label rareGift;

  @FXML
  private Label legendaryGift;

  @FXML
  private Label healthInt;

  @FXML
  private Label happinessInt;

  @FXML
  private Label energyInt;

  @FXML
  private Label fullnessInt;

  @FXML
  private Label scoreLabel;

  @FXML
  private Label message;

  @FXML
  private ImageView stateBox;

  @FXML
  private Button packButton;

  private boolean openedPack;
  private LocalDateTime timeOfLastOpenedPack;

  private final int packOpeningCoolDownInterval = 8;

  private int gameLoopInterval;

  private boolean healthWarning = false;
  private boolean energyWarning = false;
  private boolean fullnessWarning = false;
  private boolean happinessWarning = false;


  private void saveGame() {
    System.out.println("saving: " + selectedSave.get("save").get("name").asText());

    //creating nodes
    ObjectMapper mapper = JSON.getObjectMapper();

    //save node
    ObjectNode saveNode = mapper.createObjectNode();
    saveNode.put("name", selectedSave.get("save").get("name").asText());

    saveNode.put("last_played", LocalDateTime.now().format(formatter));
    saveNode.put("playtime", selectedSave.get("save").get("playtime").asInt() + millisToSeconds(System.currentTimeMillis()) - millisToSeconds((startTime)));
    // pet node
    ObjectNode petNode = mapper.createObjectNode();
    petNode.put("name", selectedSave.get("pet").get("name").asText());
    petNode.put("type", selectedSave.get("pet").get("type").asText());
    petNode.put("health", getHealth() * 100);
    petNode.put("energy", getEnergy() * 100);
    petNode.put("happiness", getHappiness() * 100);
    petNode.put("fullness", getFullness() * 100);

    //saving sub nodes to container node
    ObjectNode container = mapper.createObjectNode();
    container.put("save", saveNode);
    container.put("pet", petNode);

    container.put("day", selectedSave.get("day").asInt() + day);
    container.put("status_is_alive", !pet.getState().equals("dead"));
    container.put("score", pet.getScore());

    if (selectedSave.has("last_pack_opening")) {
      container.put("last_pack_opening", timeOfLastOpenedPack.format(formatter));
    }

    try {
      JSON.toJson("saves/" + selectedSave.get("save").get("name").asText(), container);
    } catch (IOException e) {
      System.err.println("Error creating pet: " + e.getMessage());
    }

    saveInventory();
  }


  //  time function
  private void startSession() {
    startTime = System.currentTimeMillis();
    startStatsTimer();
    startAutoSaveTimer();
    startGameLoopTimer();
  }

  private void startAutoSaveTimer() {
    autoSaveTimer = new Timeline(new KeyFrame(Duration.seconds(5 * 60), e -> {
      if (autoSaveValue) {
        System.out.println("AUTO SAVING");
        saveGame();
      }
    }
    ));
    autoSaveTimer.setCycleCount(Timeline.INDEFINITE);
    autoSaveTimer.play();
  }

  private void startGameLoopTimer() {
    gameLoopTimer = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
      packOpeningCoolDown();
    }));
    gameLoopTimer.setCycleCount(Timeline.INDEFINITE);
    gameLoopTimer.play();
  }

  private void startStatsTimer() {
    statsTimer = new Timeline(new KeyFrame(Duration.seconds(gameLoopInterval), e -> decrementStatsLoop()));
    statsTimer.setCycleCount(Timeline.INDEFINITE);
    statsTimer.play();
  }


  private void decrement() {
    String petType = selectedSave.get("pet").get("type").asText();
    switch (petType) {
      case "dog":
        if (energyBar.getProgress() <= 0 && !dogPenalty) {
          pet.setHealth(25);
          dogPenalty = true;
        }
        if (happinessBar.getProgress() >= 0) {
          pet.happinessDecrease(Math.min(1, pet.getHappiness()));
          if (fullnessBar.getProgress() <= 0) {
            pet.happinessDecrease(Math.min(4, pet.getHappiness()));
          }
        }
        if (energyBar.getProgress() >= 0 && !pet.isSleeping()) {
          pet.energyDecrease(Math.min(3, pet.getEnergy()));
        }
        if (fullnessBar.getProgress() >= 0) {
          pet.fullnessDecrease(Math.min(3, pet.getFullness()));
        }
        if (fullnessBar.getProgress() <= 0) {
          pet.healthDecrease(Math.min(3, pet.getHealth()));
        }


        break;
      case "cat":
        if (energyBar.getProgress() <= 0 && !catPenalty) {
          pet.setHealth(25);
          catPenalty = true;
        }
        if (happinessBar.getProgress() >= 0) {
          pet.happinessDecrease(Math.min(1, pet.getHappiness()));
          if (fullnessBar.getProgress() <= 0) {
            pet.happinessDecrease(Math.min(4, pet.getHappiness()));
          }
        }
        if (energyBar.getProgress() >= 0 && !pet.isSleeping()) {
          pet.energyDecrease(Math.min(3, pet.getEnergy()));
        }
        if (fullnessBar.getProgress() >= 0) {
          pet.fullnessDecrease(Math.min(3, pet.getFullness()));
        }
        if (fullnessBar.getProgress() <= 0) {
          pet.healthDecrease(Math.min(3, pet.getHealth()));
        }

        break;
      case "fish":
        if (energyBar.getProgress() <= 0 && !fishPenalty) {
          pet.setHealth(25);
          fishPenalty = true;
        }
        if (happinessBar.getProgress() >= 0) {
          pet.happinessDecrease(Math.min(1, pet.getHappiness()));
          if (fullnessBar.getProgress() <= 0) {
            pet.happinessDecrease(Math.min(4, pet.getHappiness()));
          }
        }
        if (energyBar.getProgress() >= 0 && !pet.isSleeping()) {
          pet.energyDecrease(Math.min(3, pet.getEnergy()));
        }
        if (fullnessBar.getProgress() >= 0) {
          pet.fullnessDecrease(Math.min(3, pet.getFullness()));
        }
        if (fullnessBar.getProgress() <= 0) {
          pet.healthDecrease(Math.min(3, pet.getHealth()));
        }

        break;
    }
    if (pet.getHappiness() <= 25 && !happinessWarning) {
      message.setTextFill(Color.RED);
      message.setText("WARNING!\n" + pet.getName() + "'s happiness has reached 25%");
      happinessWarning = true;
    }
    if (pet.getFullness() <= 25 && !fullnessWarning) {
      message.setTextFill(Color.RED);
      message.setText("WARNING!\n" + pet.getName() + "'s fullness has reached 25%");
      fullnessWarning = true;
    }
    if (pet.getEnergy() <= 25 && !energyWarning) {
      message.setTextFill(Color.RED);
      message.setText("WARNING!\n" + pet.getName() + "'s energy has reached 25%");
      energyWarning = true;
    }
    if (pet.getHealth() <= 25 && !healthWarning) {
      message.setTextFill(Color.RED);
      message.setText("WARNING!\n" + pet.getName() + "'s health has reached 25%");
      healthWarning = true;
    }

    checkIfDead();
    checkEnergyToSleep();
    updateStats();
  }

  private void checkIfDead() {
    if (!pet.getState().equals("dead")) {
      return;
    }
    statsTimer.stop();
    disabledAllButtons(true);
    showMainMenuDialog();
  }

  private void decrementStatsLoop() {
    System.out.println("decrement");
    decrement();
  }

  private void showMainMenuDialog() {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Pet Died!");

    ButtonType mainMenuButton = new ButtonType("Return To Main Menu", ButtonBar.ButtonData.OK_DONE);

    dialog.getDialogPane().getButtonTypes().add(mainMenuButton);

    Label label = new Label("Your Pet " + pet.getName() + " has Died!");
    dialog.getDialogPane().setContent(label);

    Platform.runLater(() -> {
      dialog.showAndWait().ifPresent(result -> {
        if (result == mainMenuButton) {
          try {
            exitButton();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    });
  }

  private void checkEnergyToSleep() {
    if (energyBar.getProgress() <= 0) {
      disabledAllButtons(false);
      goToSleep();
    }
  }

  private int millisToSeconds(long millis) {
    return (int) (millis / 1000);
  }


  private void setHealth(double health) {
    this.healthBar.setProgress(health);
  }

  private double getHealth() {
    return healthBar.getProgress();
  }

  private void setHappiness(double happiness) {
    this.happinessBar.setProgress(happiness);
  }

  private double getHappiness() {
    return happinessBar.getProgress();
  }

  private void setEnergy(double energy) {
    this.energyBar.setProgress(energy);
  }

  private double getEnergy() {
    return energyBar.getProgress();
  }

  private void setFullness(double fullness) {
    this.fullnessBar.setProgress(fullness);
  }

  private double getFullness() {
    return fullnessBar.getProgress();
  }

  private void loadGameData() {

    JsonNode petNode = selectedSave.get("pet");

//    SET CURRENT STATS
    setHealth(petNode.get("health").asDouble() / 100);
    setEnergy(petNode.get("energy").asDouble() / 100);
    setHappiness(petNode.get("happiness").asDouble() / 100);
    setFullness(petNode.get("fullness").asDouble() / 100);


//    set percentages
    healthInt.setText(petNode.get("health").asInt() + "%");
    energyInt.setText(petNode.get("energy").asInt() + "%");
    happinessInt.setText(petNode.get("happiness").asInt() + "%");
    fullnessInt.setText(petNode.get("fullness").asInt() + "%");

    lastPlayed = LocalDateTime.parse(selectedSave.get("save").get("last_played").asText(), formatter);
    day = selectedSave.get("day").asInt();

    pet.setScore(selectedSave.get("score").asInt());
    if (selectedSave.has("last_pack_opening")) {
      timeOfLastOpenedPack = LocalDateTime.parse(selectedSave.get("last_pack_opening").asText(), formatter);
    }

    if (selectedSave.has("last_pack_opening")) {
      openedPack = LocalDateTime.now().isAfter(timeOfLastOpenedPack.plusHours(packOpeningCoolDownInterval));

    }

  }

  private void loadSettings() {
    autoSaveValue = settingsInstance.getAutoSaveValue();
  }


  /**
   * This method determines whether a game is being created or loaded when entering the play scene.
   * Then, it sets the background image and sprite accordingly
   *
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
// on initialization you must grab data from previous pages using json this is easy
// for load game there is the getSelectedSave function which returns the entire JsonNode
//    this is just an example
//    change it however you'd like :D
//    IMPORTANT YOU MUST SAVE THIS RIGHT AWAY WILL BE GONE, AND gameSelectedSave WILL ONLY WORK IN INITIALIZE!!!!!1
    JsonNode loadedSave = loadGame.getSelectedSave();
    if (loadedSave != null) {
      //Code for load game here
      selectedSave = loadedSave;
    } else {
      //Code for create new game here
      selectedSave = nameSelection.getSave();
    }

    Image backgroundImage = null;
    Image spriteImage = null;
    int health = selectedSave.get("pet").get("health").asInt();
    int energy = selectedSave.get("pet").get("energy").asInt();
    int fullness = selectedSave.get("pet").get("fullness").asInt();
    int happiness = selectedSave.get("pet").get("happiness").asInt();
    String name = selectedSave.get("pet").get("name").asText();

    try {
      String petType = selectedSave.get("pet").get("type").asText();
      switch (petType) {
        case "dog":
          backgroundImage = loadImage("/com/group46/assets/images/dogBackground.png");
          spriteImage = loadImage("/com/group46/assets/pets/pixelart dog.gif");
          spriteBox.setFitWidth(450);
          pet = new Dog(name, health, fullness, happiness, energy);
          // TODO: MUST FIX
          gameLoopInterval = 3;
          break;
        case "cat":
          backgroundImage = loadImage("/com/group46/assets/images/catBackground.png");
          spriteImage = loadImage("/com/group46/assets/pets/pixelart cat.gif");
          spriteBox.setFitWidth(700);
          pet = new Cat(name, health, fullness, happiness, energy);
          // TODO: MUST FIX
          gameLoopInterval = 3;
          break;
        case "fish":
          backgroundImage = loadImage("/com/group46/assets/images/fishBackground.png");
          spriteImage = loadImage("/com/group46/assets/pets/pixelart fish.gif");
          spriteBox.setFitWidth(450);
          pet = new Fish(name, health, fullness, happiness, energy);
//          TODO: MUST FIX
          gameLoopInterval = 3;
          break;
        default:
          backgroundImage = loadImage("/com/group46/assets/images/noImageAvailable.png");
      }
    } catch (Exception e) {
      System.err.println("Error loading background image: " + e);
      backgroundImage = loadImage("/images/noImageAvailable.png");
    }

    BackgroundImage background = new BackgroundImage(
        backgroundImage,
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER,
        new BackgroundSize(
            BackgroundSize.AUTO,
            BackgroundSize.AUTO,
            false,
            false,
            true,
            false)
    );
    container.setBackground(new Background(background));
    spriteBox.setImage(spriteImage);
    loadGameData();
    loadSettings();
    parseInventory();
    packOpeningCoolDown();
//    TODO: THIS NEEDS A FIX
//    if (!lastPlayed.equals(LocalDateTime.now())) {
//      calculateStatsAfterAFK();
//    }


    App.getScene().setOnKeyPressed(this::handleKeyPress);
    startSession();
    checkIfDead();
    updateStats();
  }

  private void disabledAllButtons(boolean disabled) {
    vetButtonId.setDisable(disabled);
    bedButtonId.setDisable(disabled);
    playButtonId.setDisable(disabled);
    exerciseButtonId.setDisable(disabled);

    commonFoodButtonId.setDisable(disabled);
    rareFoodButtonId.setDisable(disabled);
    legendaryFoodButtonId.setDisable(disabled);

    commonGiftButtonId.setDisable(disabled);
    rareGiftButtonId.setDisable(disabled);
    legendaryGiftButtonId.setDisable(disabled);


  }

  //  can only open every 8 hours
  private void packOpeningCoolDown() {
    if (!selectedSave.has("last_pack_opening")) {
      return;
    }

    long period = ChronoUnit.HOURS.between(timeOfLastOpenedPack, LocalDateTime.now());
    if (period < packOpeningCoolDownInterval) {
      openedPack = true;
      packButton.setDisable(true);
    } else {
      openedPack = false;
      packButton.setDisable(false);
    }
  }

  /**
   * private helper method to load image files
   *
   * @param path
   * @return image
   */
  private Image loadImage(String path) {
    Image image = new Image(getClass().getResourceAsStream(path));
    if (image.isError()) {
      System.err.println("Error loading image: " + path);
      throw new RuntimeException("Image loading failed: " + path);
    }
    return image;
  }

  @FXML
  private void exitButton() throws IOException {
    statsTimer.stop();
    gameLoopTimer.stop();

    if (autoSaveTimer.getStatus() == Animation.Status.RUNNING) {
      autoSaveTimer.stop();
    }

    if (sleepTimer.getStatus() == Animation.Status.RUNNING) {
      sleepTimer.stop();
    }
    saveGame();
    App.setRoot("mainMenu");
  }

  private void handleKeyPress(KeyEvent KeyEvent) {
    App.getScene().setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ESCAPE) {
        try {
          exitButton();
        } catch (IOException e) {
          System.out.println(e);
        }
        event.consume();
      }
    });
  }

  /**
   * This method is called when the "play" button is clicked.
   * It executes the play function on the pet object and updates that game stats accordingly
   *
   * @throws IOException
   */
  @FXML
  private void playButton() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.play());
    updateStats();
  }

  private void goToSleep() {
    disabledAllButtons(true);
    double currentSleep = getEnergy(); // Current energy as a decimal (0.0 to 1.0)
    double diff = 1.0 - currentSleep;  // How much energy needs to be recovered

    // Calculate total time in milliseconds
    // Full recovery (diff = 1.0) should take 60 seconds (60000 ms)
    double totalRecoveryTimeMs = diff * 60000;

    // Calculate total steps for smooth animation
    int totalSteps = 100;
    double incrementAmount = diff / totalSteps;
    double stepDurationMs = totalRecoveryTimeMs / totalSteps;

    sleepTimer = new Timeline(new KeyFrame(Duration.millis(stepDurationMs), e -> {
      // Add precise increment amount
      double newProgress = energyBar.getProgress() + incrementAmount;
      // Ensure we don't exceed 1.0
      newProgress = Math.min(newProgress, 1.0);
      energyBar.setProgress(newProgress);

      // Round to whole number for percentage display
      int percentage = (int) Math.round(newProgress * 100);
      energyInt.setText(percentage + "%");

      // Set the pet's energy
      pet.setEnergy(percentage);

      System.out.println("energy bar progress: " + String.format("%.2f", newProgress));

      if (newProgress >= 1.0) {
        disabledAllButtons(false);
        pet.awake();
        sleepTimer.stop();
        updateStats();
      }
    }));

    sleepTimer.setCycleCount(Timeline.INDEFINITE);
    sleepTimer.play();
    pet.goToBed();
    updateStats();
  }


  /**
   * This method is called when the "bed" button is clicked.
   * It executes the goToBed function on the pet object and updates that game stats accordingly
   *
   * @throws IOException
   */
  @FXML
  private void bedButton() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.goToBed());

    goToSleep();
    updateStats();
  }

  /**
   * This method is called when the "exercise" button is clicked.
   * It executes the exercise function on the pet object and updates that game stats accordingly
   *
   * @throws IOException
   */
  @FXML
  private void exerciseButton() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.exercise());
    updateStats();
  }

  /**
   * This method is called when the "vet" button is clicked.
   * It executes the vet function on the pet object and updates that game stats accordingly
   *
   * @throws IOException
   */
  @FXML
  private void vetButton() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.vet());
    updateStats();
  }

  //  TODO: optimize this function
  private void calculateStatsAfterAFK() {
    System.out.println("CALCULATING AFTER AFK");
    LocalDateTime currentTime = LocalDateTime.now();


    long periodBetweenLastAndCurrentInDays = ChronoUnit.DAYS.between(lastPlayed, currentTime);
    day = (int) periodBetweenLastAndCurrentInDays;

    long periodBetweenLastAndCurrentInSeconds = ChronoUnit.SECONDS.between(lastPlayed, currentTime);
    int loops = (int) periodBetweenLastAndCurrentInSeconds / gameLoopInterval;
    for (int i = 0; i < loops; i++) {
      decrement();
    }
    checkEnergyToSleep();
  }


  private void parseInventory() {
    try {
      JsonNode inventoryJson = JSON.parse("inventory");

      inventory.setGifts("common", inventoryJson.get("gifts").get("common").asInt());
      inventory.setGifts("rare", inventoryJson.get("gifts").get("rare").asInt());
      inventory.setGifts("legendary", inventoryJson.get("gifts").get("legendary").asInt());

      inventory.setFood("common", inventoryJson.get("foods").get("common").asInt());
      inventory.setFood("rare", inventoryJson.get("foods").get("rare").asInt());
      inventory.setFood("legendary", inventoryJson.get("foods").get("legendary").asInt());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  private void saveInventory() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode container = mapper.createObjectNode();

    ObjectNode gifts = mapper.createObjectNode();
    gifts.put("common", inventory.getNumberOfGifts("common"));
    gifts.put("rare", inventory.getNumberOfGifts("rare"));
    gifts.put("legendary", inventory.getNumberOfGifts("legendary"));

    ObjectNode food = mapper.createObjectNode();
    food.put("common", inventory.getNumberOfFood("common"));
    food.put("rare", inventory.getNumberOfFood("rare"));
    food.put("legendary", inventory.getNumberOfFood("legendary"));

    container.put("gifts", gifts);
    container.put("foods", food);

    try {
      JSON.toJson("inventory", container);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * This method saves all vital stats to the pets JSON file
   * and also displays their updated values on the screen
   */
  private void updateStats() {
    //updating labels and progress bars on gui
    happinessBar.setProgress((double) pet.getHappiness() / 100);
    energyBar.setProgress((double) pet.getEnergy() / 100);
    fullnessBar.setProgress((double) pet.getFullness() / 100);
    healthBar.setProgress((double) pet.getHealth() / 100);
    happinessInt.setText(pet.getHappiness() + "%");
    energyInt.setText(pet.getEnergy() + "%");
    fullnessInt.setText(pet.getFullness() + "%");
    healthInt.setText(pet.getHealth() + "%");
    scoreLabel.setText(String.valueOf(pet.getScore()));
    commonFood.setText(String.valueOf(inventory.getNumberOfFood("common")));
    rareFood.setText(String.valueOf(inventory.getNumberOfFood("rare")));
    legendaryFood.setText(String.valueOf(inventory.getNumberOfFood("legendary")));
    commonGift.setText(String.valueOf(inventory.getNumberOfGifts("common")));
    rareGift.setText(String.valueOf(inventory.getNumberOfGifts("rare")));
    legendaryGift.setText(String.valueOf(inventory.getNumberOfGifts("legendary")));


    //updating state and emoji image
    String state = pet.getState();
    Image stateImage;
    switch (state) {
      case "dead":
        stateImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/Dead emoji.png"));
        break;
      case "sleeping":
        stateImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/Sleeping emoji.png"));
        break;
      case "angry":
        stateImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/Angry emoji.png"));
        break;
      case "hangry":
        stateImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/Angry emoji.png"));
        break;
      case "hungry":
        stateImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/Hungry emoji.png"));
        break;
      case "normal":
        stateImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/Happy emoji.png"));
        break;
      default:
        stateImage = new Image(getClass().getResourceAsStream("/com/group46/assets/images/Happy emoji.png"));
        break;
    }
    stateBox.setImage(stateImage);


    //Reset warning flags when necessary
    if (pet.getHappiness() > 25) happinessWarning = false;
    if (pet.getFullness() > 25) fullnessWarning = false;
    if (pet.getEnergy() > 25) energyWarning = false;
    if (pet.getHealth() > 25) healthWarning = false;


//    saveGame();
  }


  /**
   * This function decrements the food count and feeds the pet using the feed function
   *
   * @throws IOException
   */
  @FXML
  private void giveCommonFood() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.feed("common"));
    updateStats();
  }

  /**
   * This function decrements the food count and feeds the pet using the feed function
   *
   * @throws IOException
   */
  @FXML
  private void giveRareFood() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.feed("rare"));
    updateStats();
  }

  /**
   * This function decrements the food count and feeds the pet using the feed function
   *
   * @throws IOException
   */
  @FXML
  private void giveLegendaryFood() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.feed("legendary"));
    updateStats();
  }

  /**
   * This function decrements the gift count and gifts the pet using the gift function
   *
   * @throws IOException
   */
  @FXML
  private void giveCommonGift() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.gift("common"));
    updateStats();
  }

  /**
   * This function decrements the gift count and gifts the pet using the gift function
   *
   * @throws IOException
   */
  @FXML
  private void giveRareGift() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.gift("rare"));
    updateStats();
  }

  /**
   * This function decrements the gift count and gifts the pet using the gift function
   *
   * @throws IOException
   */
  @FXML
  private void giveLegendaryGift() throws IOException {
    message.setTextFill(Color.WHITE);
    message.setText(pet.gift("legendary"));
    updateStats();
  }

  @FXML
  private void packOpening() throws IOException {
    message.setTextFill(Color.GOLD);
    message.setText(generateGift());
    packButton.setDisable(true);
    openedPack = true;
    timeOfLastOpenedPack = LocalDateTime.now();
    saveGame();
    updateStats();
  }
}


