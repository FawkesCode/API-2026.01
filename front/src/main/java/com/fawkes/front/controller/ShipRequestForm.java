package com.fawkes.front.controller;

import com.fawkes.front.models.Order;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ShipRequestForm {

    @FXML private Label    detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea  descriptionField;
    @FXML private Label     errorLabel;

    private Order    order;
    private Runnable onSaveSuccess;
    private final UserInfoManager loggedUser = UserInfoManager.getInstance();

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    public void initialize() {
        btnCommand.setText("Confirmar Envio");
        if (descriptionField != null)
            descriptionField.setPromptText("Observações sobre o envio (opcional)");
    }

    public void setData(Order order) {
        this.order = order;
        String fornecedor = (order.getSuppliersList() != null && !order.getSuppliersList().isEmpty())
                ? order.getSuppliersList().get(0).getSupplierName() : "fornecedor";

        detailsLabel.setText("Confirmar que o pedido #" + order.getId()
                + " foi enviado por " + fornecedor
                + ". Esta ação indica que a mercadoria está a caminho."
                + " Registrado por " + loggedUser.getUserName() + ".");
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        try {
            ApiClient.post("/api/purchase-orders/" + order.getId() + "/ship", "{}");
            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            if (errorLabel != null) errorLabel.setText("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}