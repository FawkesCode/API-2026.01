package com.fawkes.front.controller;

import com.fawkes.front.MainApplication;
import com.fawkes.front.service.ApiClient;
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
        // Validate fields are not empty
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, preencha todos os campos!");
            return;
        }

        // Try to authenticate with backend API
        try {
            ApiClient.login(email, password);
            // If login succeeds, navigate to app
            Stage curStage = (Stage) buttonSubmit.getScene().getWindow();
            nm.navigateToApp(curStage);
        } catch (Exception e) {
            // Handle login errors
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("404")) {
                errorLabel.setText("Usuário não encontrado. Verifique o email ou registre-se.");
            } else if (errorMsg != null && errorMsg.contains("401")) {
                errorLabel.setText("Email ou senha de usuário incorretos!");
            } else if (errorMsg != null && errorMsg.contains("403")) {
                errorLabel.setText("Acesso negado! Você não tem permissão.");
            } else if (errorMsg != null && errorMsg.contains("No authentication token")) {
                errorLabel.setText("Erro ao fazer login. Tente novamente.");
            } else {
                errorLabel.setText("Erro ao conectar ao servidor: " + (errorMsg != null ? errorMsg : "Desconhecido"));
            }
        }
    }

    public void userLogin(ActionEvent event) throws IOException {
        checkLogin(emailField.getText(), passwordField.getText());
    }


}
