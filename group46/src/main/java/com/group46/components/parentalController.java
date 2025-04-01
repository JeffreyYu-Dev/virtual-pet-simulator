package com.group46.components;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group46.App;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class parentalController {

  private JsonNode parsedParental;
  private static parentalController instance;
  private long sessionStartTimeMillis;

  private parentalController(){
    loadParental();
  }

  public static parentalController getInstance() {
    if (instance == null) {
      instance = new parentalController();
    }

    return instance;
  }

  public boolean getEnableTimeVal(){
    return this.parsedParental.has("Enable") && this.parsedParental.get("Enable").asBoolean();
  }

  public void updateParsedParental() {
    try {
      this.parsedParental = JSON.parse("parental");
    } catch (IOException e) {
      System.out.println("Error in parsing settings in updateParsedSettings.");
      throw new RuntimeException(e);
    }
  }
  
//======================================================================================//
  @FXML
  private void loadParental(){
    Path filePath = Paths.get(JSON.getDatabaseFolder(), "parental.json");

    
    if (!Files.exists(filePath)) {

      ObjectMapper mapper = JSON.getObjectMapper();

      ObjectNode parental = mapper.createObjectNode();
      parental.put("Enable", true);
      parental.put("Total", 0);
    
    try {
      JSON.toJson("parental", parental);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  try {
    this.parsedParental = JSON.parse("parental");
  } catch (IOException e) {
    throw new RuntimeException(e);
    }
  }

  //====================================================================================================//

  public void saveEnableTimeVal(boolean enabled) {
    ObjectMapper mapper = JSON.getObjectMapper();
    ObjectNode updated = mapper.createObjectNode();
    updated.put("Enable", enabled);
    updated.put("Total", getTotalTime());

    if (parsedParental.has("Dates")) {
      updated.set("Dates", parsedParental.get("Dates"));
  }

    try {
      JSON.toJson("parental", updated);
      
      this.parsedParental = updated;
    } catch (IOException e) {
        System.out.println(" Failed to save 'Enable' state to parental.json.");
        e.printStackTrace();
    }
}

public void saveTotal(long total){
  ObjectMapper mapper = JSON.getObjectMapper();
  ObjectNode updated = mapper.createObjectNode();
  updated.put("Total", total);
  updated.put("Enable", getEnableTimeVal());

  if (parsedParental.has("Dates")) {
    updated.set("Dates", parsedParental.get("Dates"));
}

  try{
    JSON.toJson("parental", updated);

    this.parsedParental = updated;
  }
  catch(IOException e){
    System.out.println("Failed to save 'Total' ");
    e.printStackTrace();
  }
}


//==========================================================================================//
    //should show a pop up that asks for the password when the page is opened 
   //change some of the code from abdul to abdul
   @FXML
  public static void showPasswordPrompt(){
    Dialog<String> dialog=new Dialog<>();
    dialog.setTitle("Please Enter Password");

    ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Enter password here");

    VBox content = new VBox(10);
    content.getChildren().addAll(new Label("Password:"), passwordField);
    dialog.getDialogPane().setContent(content);

    dialog.setResultConverter(new javafx.util.Callback<ButtonType, String>() {
    @Override
    public String call(ButtonType dialogButton) {
        if (dialogButton == submitButton) {
            return passwordField.getText();
        }
        return null;
    }
});

  Optional<String> result = dialog.showAndWait();

  if (result.isPresent()) {
      String password = result.get();
      if (!password.equals("1234")) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Access Denied");
          alert.setHeaderText(null);
          alert.setContentText("Incorrect password. Returning to main menu.");
          alert.showAndWait();
          try{
          App.setRoot("mainMenu");
          }catch (IOException e){
            e.printStackTrace();
          }
      }
      else {
        try{
          App.setRoot("parentalControl");
      }catch (IOException e){
      e.printStackTrace();
      }
    }
  }

  }
//===================================================================================================//

//====================================================================================================//
  public long getTotalTime(){
    if(this.parsedParental.has("Total")){
      return this.parsedParental.get("Total").asLong();
    }
    else{
      return 0;
    }
  }
//=================================================================================================//

public void startSessionTimer() {
  sessionStartTimeMillis = System.currentTimeMillis();
}
public void saveSessionOnExit() {
  long sessionDuration = System.currentTimeMillis() - sessionStartTimeMillis;
  long updatedTotal = getTotalTime() + sessionDuration;
  System.out.println("Saved total time: " + updatedTotal); // debug
  saveTotal(updatedTotal);
}
public long getTimeSession(){
  long elapsed = System.currentTimeMillis() - sessionStartTimeMillis;
  return getTotalTime() + elapsed;
}

public int getPlayDayCount() {
  if (parsedParental.has("Dates")) {
      return parsedParental.get("Dates").size();
  }
  return 0;
}


public void getDate(){
  LocalDate myDate = LocalDate.now();
  String myDateStr = myDate.toString();

  ObjectMapper mapper = JSON.getObjectMapper();
  ObjectNode updated = mapper.createObjectNode();

  ArrayList<String> Dates = new ArrayList<>();
  if (parsedParental.has("Dates")) {
    for (JsonNode node : parsedParental.get("Dates")) {
        Dates.add(node.asText());
    }
}
  if(!Dates.contains(myDateStr)){
    Dates.add(myDateStr);
  }

    updated.put("Enable", getEnableTimeVal());
    updated.put("Total", getTotalTime());

    ArrayNode playDaysNode = mapper.createArrayNode();
    for (String day : Dates) {
        playDaysNode.add(day);
    }
    updated.set("Dates", playDaysNode);

    try {
        JSON.toJson("parental", updated);
        this.parsedParental = updated;
    } catch (IOException e) {
        System.out.println("Failed to save updated play days.");
        e.printStackTrace();
    }
}


public void resetAllData() {
  ObjectMapper mapper = JSON.getObjectMapper();
  ObjectNode updated = mapper.createObjectNode();

  updated.put("Enable", getEnableTimeVal());
  updated.put("Total", 0);

  updated.put("From", getFromTime());
  updated.put("To", getToTime());

  ArrayNode datesArray = mapper.createArrayNode();
  String today = LocalDate.now().toString();
  datesArray.add(today);
  updated.set("Dates", datesArray);

  try {
      JSON.toJson("parental", updated);
      this.parsedParental = updated;
  } catch (IOException e) {
      System.out.println("Failed to reset parental data.");
      e.printStackTrace();
  }
}

public static boolean showTimeLimitPopup(Stage ownerStage) {
  Dialog<String> dialog = new Dialog<>();
  dialog.setTitle("Time Limit Reached");
  dialog.setHeaderText("Your time is up! Please enter the password to continue.");
  dialog.initOwner(ownerStage);
  dialog.setResizable(false);

  PasswordField passwordField = new PasswordField();
  passwordField.setPromptText("Enter password");

  VBox content = new VBox(10);
  content.getChildren().addAll(new Label("Password:"), passwordField);
  dialog.getDialogPane().setContent(content);

  ButtonType unlockButton = new ButtonType("Unlock", ButtonBar.ButtonData.OK_DONE);
  dialog.getDialogPane().getButtonTypes().addAll(unlockButton);

  final boolean[] success = {false};

  dialog.getDialogPane().lookupButton(unlockButton).addEventFilter(ActionEvent.ACTION, event -> {
      String entered = passwordField.getText();
      if (!entered.equals("1234")) {
          event.consume();
          Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect password. Try again.");
          alert.initOwner(ownerStage);
          alert.showAndWait();
      } else {
          success[0] = true;
      }
  });

  dialog.showAndWait();
  return success[0];
}

public void saveTimeRange(String from, String to) {
  ObjectMapper mapper = JSON.getObjectMapper();
  ObjectNode updated = mapper.createObjectNode();

  updated.put("Enable", getEnableTimeVal());
  updated.put("Total", getTotalTime());
  updated.put("From", from);
  updated.put("To", to);

  if (parsedParental.has("Dates")) {
      updated.set("Dates", parsedParental.get("Dates"));
  }

  try {
      JSON.toJson("parental", updated);
      this.parsedParental = updated;
  } catch (IOException e) {
      System.out.println("Failed to save time range.");
      e.printStackTrace();
  }
}

public String getFromTime() {
  return parsedParental.has("From") ? parsedParental.get("From").asText() : "";
}

public String getToTime() {
  return parsedParental.has("To") ? parsedParental.get("To").asText() : "";
}

}
