package com.fawkes.front.controller;

import com.fawkes.front.models.Supplier;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;

public class EditSupplierForm {
    @FXML
    private JFXButton btnClose;
    @FXML private Label errorLabel;

    @FXML private TextField cnpjField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> paymentField;
    @FXML private CheckBox activeCheckBox;
    @FXML private JFXButton submmitForm;
    @FXML private Label cnpjLabel;
    @FXML private Label nameLabel;
    @FXML private Label methodLabel;

    private List<String> PAYMENTS = Arrays.asList("PIX", "CREDITO", "DEBITO", "BOLETO");

    private Runnable onSaveSuccess;
    private Supplier supplier;

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

        activeCheckBox.setVisible(true);
        activeCheckBox.setManaged(true);

        submmitForm.setText("Salvar alterações");
        cnpjLabel.setText("CNPJ:");
        nameLabel.setText("Nome:");
        methodLabel.setText("Método de Pag.:");

    }

    public void setSupplierData(Supplier sup) {
        this.supplier = sup;
        cnpjField.setText(sup.getCnpj());
        nameField.setText(sup.getName());
        paymentField.getSelectionModel().select(sup.getPaymentMethod());
        activeCheckBox.setSelected(sup.getActive().equals("Ativo"));
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
        boolean ativo    = activeCheckBox.isSelected();

        try {
            String jsonBody = "{"
                    + "\"supplierName\":\"" + nome + "\","
                    + "\"cnpj\":\"" + cnpj + "\","
                    + "\"paymentMethods\":[\"" + pagamento + "\"],"
                    + "\"isActive\":" + ativo
                    + "}";

            ApiClient.put("/api/suppliers/" + supplier.getId(), jsonBody);

            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }

    }
}
