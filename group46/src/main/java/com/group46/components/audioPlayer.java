package com.group46.components;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class audioPlayer {

  public static audioPlayer instance;

  private MediaPlayer mediaPlayer;


  public void setMedia(String fileName) {
    try {
      Media media = new Media(new File(getClass().getResource("/com/group46/assets/" + fileName + ".mp3").toURI()).toURL().toString());
      mediaPlayer = new MediaPlayer(media);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  public static audioPlayer getInstance() {
    if (instance == null) {
      instance = new audioPlayer();
    }
    return instance;
  }

}


