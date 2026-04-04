package com.fawkes.front.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public class AddSupplierForm {
    @FXML private JFXButton btnClose;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private TextField cnpjField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> paymentField;

    private String applyCNPJMask(String s) {
        String m = s;
        if (m.length() > 2) m = m.substring(0, 2) + "." + m.substring(2);
        if (m.length() > 6) m = m.substring(0, 6) + "." + m.substring(6);
        if (m.length() > 10) m = m.substring(0, 10) + "/" + m.substring(10);
        if (m.length() > 15) m = m.substring(0, 15) + "-" + m.substring(15);
        return m;
    }

    @FXML
    public void initialize() {
        cnpjField.textProperty().addListener((observable, oldVal, newVal) -> {
            String onlyNum = newVal.replaceAll("\\D", "");

            if (onlyNum.length() > 14) {
                onlyNum = onlyNum.substring(0, 14);
            }

            String mask = applyCNPJMask(onlyNum);

            if (!newVal.equals(mask)) {
                cnpjField.setText(mask);
                cnpjField.positionCaret(mask.length());
            }
        } );
    }



    @FXML
    private void closeModal(ActionEvent event) {
        ((Stage) btnClose.getScene().getWindow()).close();
    }

    @FXML
    private void handleOnSubmit(ActionEvent event) {
        if (nameField.getText().isEmpty() || cnpjField.getText().isEmpty() || paymentField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
        } else {
            System.out.println("Fazer a lógica de upload aqui");
        }

    }
}
