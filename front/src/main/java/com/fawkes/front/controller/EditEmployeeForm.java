package com.fawkes.front.controller;

import com.fawkes.front.utils.PasswordFieldSkin;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class EditEmployeeForm {
    @FXML private JFXButton btnClose;

    // FORM INPUTS
    @FXML private ToggleButton statusField;
    @FXML private ImageView employeeView;
    @FXML private TextField emailField;
    @FXML private TextField positionField;
    @FXML private ComboBox<String> userGroupField;
    @FXML private ComboBox<String> departmentField;

    public void initialize() {
        statusField.setSelected(true);
        statusField.setText("Ativo");

        statusField.selectedProperty().addListener((observable, OldVal, newVal)-> {
            if (newVal) {
                statusField.setText("Ativo");
            } else {
                statusField.setText("Inativo");
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

}
