module com.group46 {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.fasterxml.jackson.databind;
  requires java.sql;
  requires java.desktop;
  requires javafx.media;

  opens com.group46 to javafx.fxml, com.fasterxml.jackson.databind;
  exports com.group46;
  exports com.group46.controllers;
  opens com.group46.controllers to javafx.fxml;
  exports com.group46.interfaces;
  opens com.group46.interfaces to javafx.fxml;

}