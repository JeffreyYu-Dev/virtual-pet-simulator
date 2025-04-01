package com.group46.controllers;

import com.group46.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Jagger Adams
 */

public class tutorial implements Initializable {
    @FXML

    /**
     * Declares instance variables for the FXML label whose text will be changed and
     * and string array for the 5 messages to be displayed (stepped through).
     */
    private Label tutorialText;
    private String [] steps = {"Step 1:\n\n\n\n\n\nSelect 'New' to create a new pet OR 'Load' to load an existing pet"
            , "Step 2:\n\nIf you selected 'New':\nFirst, you must select the animal you want your pet to be. Then, " +
            "you must enter a name for your pet and click 'Create!'.\n\n\n\nIf you selected 'Load':\nSelect a previously" +
            " created pet life to interact with. If you have no saved pets, click 'Back' and create a new one.",
            "Step 3:\n\n\n\n\n\n\nFrequently give your pet food/gift items to ensure their vital stats (e.g. Happiness, fullness," +
                    " etc.) don't decline", "Step 4:\n\n\n\n\nPlay with your pet to increase their happiness. Put your pet to sleep" +
            " to replenish their energy bar.\n\nAll pet interactions increase your score.", "Step 5:\n\n\n\n\n\n\nIf a pet has died, ask a" +
            " parent to revive them via the 'Parental Controls' option on the main menu."};
    private int index = 0;


    /**
     * This method just makes the label start with the first message in the array
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tutorialText.setText(steps[index]);
    }

    /**
     * This method changes the scene back to the main menu when the user selects the
     * 'back' button
     * @throws IOException
     */
    @FXML
    public void goBack() throws IOException {
        App.setRoot("mainMenu");
    }

    /**
     * This method switches the message back one index/step
     * @throws IOException
     */
    @FXML
    public void previous() throws IOException {
        if (index > 0){
            index--;
            tutorialText.setText(steps[index]);
        }
    }

    /**
     * This method switches the message forward one index/step
     * @throws IOException
     */
    @FXML
    public void next() throws IOException {
        if (index < steps.length - 1){
            index++;
            tutorialText.setText(steps[index]);
        }
    }

}
