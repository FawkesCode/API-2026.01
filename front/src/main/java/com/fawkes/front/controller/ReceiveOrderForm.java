package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReceiveOrderForm {

    @FXML private Label detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea descriptionField;
    @FXML private Label errorLabel;

    UserInfoManager loggedUser = UserInfoManager.getInstance();

    private Runnable onSaveSuccess;
    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    private Order order;

    public void initialize() {
        btnCommand.setText("Confirmar Recebimento");
    }

    public void setData(Order order) {
        this.order = order;

        StringBuilder productsList = new StringBuilder();
        for (RequestItem pro : order.getItemsList()) {
            productsList.append(pro.getProduct().getName()).append(", ");
        }
        if (productsList.length() > 2) {
            productsList.setLength(productsList.length() - 2);
        }

        String text = "Confirmar recebimento do pedido de "
                + order.getQuantity() + " itens: " + productsList
                + ". Total: " + order.getTotalValue()
                + ". Pedido feito por " + order.getRequesterName()
                + " para o setor " + order.getSector().toUpperCase() + ".";

        detailsLabel.setText(text);
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        try {
            JsonNode response = ApiClient.post("/api/purchase-orders/" + order.getId() + "/receive", "{}");
            System.out.println("Recebimento confirmado: " + response.toPrettyString());

            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }
            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}