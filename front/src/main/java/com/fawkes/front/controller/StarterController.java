package com.fawkes.front.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static javafx.application.Application.launch;

public class StarterController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public static void main(String[] args) {
        launch();
    }
}
