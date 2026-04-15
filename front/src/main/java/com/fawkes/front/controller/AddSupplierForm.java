package com.fawkes.front.controller;

import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public class AddSupplierForm {
    @FXML private JFXButton btnClose;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private TextField cnpjField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> paymentField;

    private List<String> PAYMENTS = Arrays.asList("PIX", "CREDITO", "DEBITO", "BOLETO");

    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

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

        // COMBO BOX CONTENT
        paymentField.getItems().addAll(PAYMENTS);

        paymentField.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String payment) {
                return StringUtils.paymentTranslation(payment);
            }

            @Override
            public String fromString(String s) {
                return null;
            }
        });

        paymentField.setCellFactory(v -> new ListCell<String>() {
            @Override
            protected void  updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : StringUtils.paymentTranslation(item));
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
    private void handleOnSubmit(ActionEvent event) {
        if (nameField.getText().isEmpty() || cnpjField.getText().isEmpty() || paymentField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
            return;
        }
        errorLabel.setText("");

        String nome      = nameField.getText().trim();
        String cnpj      = cnpjField.getText().trim();
        String pagamento = paymentField.getValue();

        try {
            String body = String.format(
                    "{\"nomeFornecedor\":\"%s\",\"cnpjFornecedor\":\"%s\",\"meioPagamento\":\"%s\"}",
                    nome, cnpj, pagamento);
            ApiClient.post("/api/suppliers", body);

            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }

    }
}
