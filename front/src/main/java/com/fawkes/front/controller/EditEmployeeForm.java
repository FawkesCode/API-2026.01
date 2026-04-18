package com.fawkes.front.controller;

import com.fawkes.front.models.Employee;
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

public class EditEmployeeForm {
    @FXML private JFXButton btnClose;

    // FORM INPUTS
    @FXML private ToggleButton statusField;
    @FXML private ImageView employeeView;
    @FXML private TextField emailField;

    @FXML private ComboBox<String> userGroupField;
    @FXML private TextField departmentField;
    @FXML private TextField nameField;
    @FXML private Label errorLabel;

    private Employee employee;
    private static final List<String> GROUPS = Arrays.asList("DIRECTOR", "MANAGER", "OPERATIONAL");

    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

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
        System.out.println(userGroupField.getItems());
    }

    public void setEmployeeData(Employee emp) {
        this.employee = emp;
        nameField.setText(emp.getName());
        emailField.setText(emp.getEmail());
        departmentField.setText(emp.getDepartment());
        userGroupField.getSelectionModel().select(emp.getPosition());
        employeeView.setImage(emp.getPicture());


        if (emp.getStatus().toLowerCase().equals("ativo")) {
            statusField.setSelected(true);
        } else {
            statusField.setSelected(false);
        }

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
        errorLabel.setText("");


        String username = nameField.getText().trim();
        String email = emailField.getText().trim();
        String group = userGroupField.getSelectionModel().getSelectedItem();
        String dept = departmentField.getText().trim();

        try {
            System.out.println("Formulário enviado");
            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}
