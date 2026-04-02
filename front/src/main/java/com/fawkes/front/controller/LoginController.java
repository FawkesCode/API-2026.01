package com.fawkes.front.controller;

import com.fawkes.front.MainApplication;
import com.fawkes.front.utils.NavigationManager;
import com.fawkes.front.utils.PasswordFieldSkin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ToggleButton btnTogglePassword;
    @FXML private ImageView btnToggleIcon;
    @FXML private Label errorLabel;
    @FXML private Button buttonSubmit;

    private String testEmail = "teste.123@gmail.com";
    private String testPassword = "teste123";

    private final Image eyeOpen = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/eye-open.png")));
    private final Image eyeClosed = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/eye-closed.png")));

    NavigationManager nm = new NavigationManager();

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

    private void checkLogin(String email, String password) throws IOException {
        if (email.equals(testEmail) && password.equals(testPassword)) {
            Stage curStage = (Stage) buttonSubmit.getScene().getWindow();
            nm.navigateToApp(curStage);
        } else {
            errorLabel.setText("Email ou senha de usuário incorretos!");
        }

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, preencha todos os campos!");
        }
    }

    public void userLogin(ActionEvent event) throws IOException {
        checkLogin(emailField.getText(), passwordField.getText());
    }


}
