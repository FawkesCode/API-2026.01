package com.fawkes.front.controller;

import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.PasswordFieldSkin;
import com.fawkes.front.utils.StringUtils;
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
import javafx.util.StringConverter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AddEmployeeForm {

    @FXML private ToggleButton btnTogglePassword;
    @FXML private ImageView btnToggleIcon;
    @FXML private Button btnClose;
    @FXML private Button btnConfirm;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private PasswordField passwordField;
    @FXML private ImageView employeeView;
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private TextField departmentField;
    @FXML private ComboBox<String> userGroupField;


    // COMBO BOX VALUES
    private static final List<String> GROUPS = Arrays.asList("DIRECTOR", "MANAGER", "OPERATIONAL");

    private final Image eyeOpen = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/eye-open.png")));
    private final Image eyeClosed = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/eye-closed.png")));

    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    public void initialize() {
        passwordField.setSkin(new PasswordFieldSkin(passwordField, btnTogglePassword));

        btnTogglePassword.selectedProperty().addListener((obs, wasSelected, isSelected)-> {
            if(isSelected) {
                btnToggleIcon.setImage(eyeOpen);
            } else {
                btnToggleIcon.setImage(eyeClosed);
            }
        });

        //COMBO BOX CONTENT
        userGroupField.getItems().addAll(GROUPS);

        userGroupField.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String role) {
                return StringUtils.roleTranslation(role);
            }

            @Override
            public String fromString(String s) {
                return null;
            }
        });

        userGroupField.setCellFactory(v -> new ListCell<String>() {
            @Override
            protected void  updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : StringUtils.roleTranslation(item));
            }
        });
    }

    @FXML
    private void closeModal(ActionEvent event) {
        ((Stage) btnClose.getScene().getWindow()).close();
    }

    @FXML
    private void handleCloseModal() {
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
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty() || nameField.getText() .isEmpty() || userGroupField.getSelectionModel().getSelectedItem() == null ) {
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
            return;
        }
        errorLabel.setText("");


        String username = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String group    = userGroupField.getSelectionModel().getSelectedItem();
        String dept     = departmentField.getText().trim();

        try {
            String body = String.format(
                    "{\"userName\":\"%s\",\"userMail\":\"%s\",\"password\":\"%s\"," +
                            "\"isActive\":true,\"role\":\"%s\",\"departamentName\":\"%s\"}",
                    username, email, password, group, dept);
            ApiClient.post("/api/users", body);

            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }



    }



}
