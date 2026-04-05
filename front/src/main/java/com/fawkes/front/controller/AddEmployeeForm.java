package com.fawkes.front.controller;

import com.fawkes.front.utils.PasswordFieldSkin;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.Objects;

public class AddEmployeeForm {

    @FXML private ToggleButton btnTogglePassword;
    @FXML private ImageView btnToggleIcon;
    @FXML private JFXButton btnClose;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private PasswordField passwordField;
    @FXML private ImageView employeeView;
    @FXML private TextField emailField;
    @FXML private TextField positionField;
    @FXML private ComboBox<String> userGroupField;
    @FXML private ComboBox<String> departmentField;

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

    @FXML
    private void closeModal(ActionEvent event) {
        ((Stage) btnClose.getScene().getWindow()).close();
    }

    @FXML
    private void handleOpenExplorer(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar a Imagem do Funcionário");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.png", "*.jpg", "*.jpeg"));

        Window stage = ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            employeeView.setImage(image);
        }
    }
    @FXML
    private void handleOnSubmit(ActionEvent event) {
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty() || positionField.getText().isEmpty() || userGroupField.getSelectionModel().getSelectedItem() == null || departmentField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
        } else {
            System.out.println("Fazer a lógica de upload aqui");
        }

    }

}
