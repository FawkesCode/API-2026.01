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


public class DeclineRequestForm {
    @FXML private Label detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea descriptionField;
    @FXML private Label errorLabel;
    UserInfoManager loggedUser = UserInfoManager.getInstance();

    private Order order;
    private Runnable onSaveSuccess;
    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }
    public void initialize() {
        btnCommand.setText("Recusar");
    }


    public void setData(Order order) {

        StringBuilder productsList = new StringBuilder();
        for (RequestItem pro : order.getItemsList()) {
            productsList.append(pro.getProduct().getName());
            productsList.append(",");
        }

        if (productsList.length() > 2) {
            productsList.setLength(productsList.length() - 2);
        }


        String text = "Pedido de " + order.getQuantity() + " itens, sendo eles: " + productsList + "; Total de " + order.getTotalValue() + ". Compra pedida pelo " + order.getRequesterName() + " para o setor " + order.getSector().toUpperCase() + ". Solicitação sendo recusada por " + loggedUser.getUserName() + ".";
        this.order = order;

        detailsLabel.setText(text);
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode body = mapper.createObjectNode();
            String reason = descriptionField.getText();
            if (reason != null && !reason.isBlank()) body.put("reason", reason.trim());

            JsonNode response = ApiClient.post("/api/purchase-orders/" + order.getId() + "/cancel",
                    mapper.writeValueAsString(body));
            System.out.println("RETORNO DO BACKEND: " + response.toPrettyString());

            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}

