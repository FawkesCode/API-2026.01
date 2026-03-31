package com.fawkes.front.controller;

import com.fawkes.front.utils.PasswordFieldSkin;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class LoginController {
    @FXML private PasswordField passwordField;
    @FXML private ToggleButton btnTogglePassword;
    @FXML private ImageView btnToggleIcon;

    private final Image eyeOpen = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/eye-open.png")));
    private final Image eyeClosed = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/eye-closed.png")));

    public void initialize() {
        passwordField.setSkin(new PasswordFieldSkin(passwordField, btnTogglePassword));

        btnTogglePassword.selectedProperty().addListener((obs, wasSelected, isSelected)-> {
            if(isSelected) {
                btnToggleIcon.setImage(eyeOpen);
            } else {
                btnToggleIcon.setImage(eyeClosed);
            }
        });

    }


}
