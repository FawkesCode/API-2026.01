package com.fawkes.front.controller;

import com.fawkes.front.models.Order;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ProblemRequestForm {

    @FXML private Label     detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea  descriptionField;
    @FXML private Label     errorLabel;

    private Order    order;
    private Runnable onSaveSuccess;
    private final UserInfoManager loggedUser = UserInfoManager.getInstance();

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    public void initialize() {
        btnCommand.setText("Reportar Problema");
        if (descriptionField != null)
            descriptionField.setPromptText("Descreva o problema encontrado no recebimento...");
    }

    public void setData(Order order) {
        this.order = order;
        detailsLabel.setText("Reportar problema no recebimento do pedido #" + order.getId()
                + ". Informe o problema encontrado. Registrado por "
                + loggedUser.getUserName() + ".");
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        String reason = descriptionField != null ? descriptionField.getText().trim() : "";
        if (reason.isEmpty()) {
            if (errorLabel != null) errorLabel.setText("Descreva o problema.");
            return;
        }
        try {
            String body = "{\"reason\": \"" + reason.replace("\"", "'") + "\"}";
            ApiClient.post("/api/purchase-orders/" + order.getId() + "/problem", body);
            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            if (errorLabel != null) errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}