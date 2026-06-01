package com.fawkes.front.controller;

import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReceiveRequestForm {

    @FXML private Label     detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea  descriptionField;   // usado como campo de nota fiscal
    @FXML private Label     errorLabel;

    private Order    order;
    private Runnable onSaveSuccess;
    private final UserInfoManager loggedUser = UserInfoManager.getInstance();
    private final ObjectMapper    mapper     = new ObjectMapper();

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    public void initialize() {
        btnCommand.setText("Confirmar Recebimento");
        if (descriptionField != null)
            descriptionField.setPromptText("Número da nota fiscal (obrigatório)");
    }

    public void setData(Order order) {
        this.order = order;
        detailsLabel.setText("Registrar o recebimento do pedido #" + order.getId()
                + ". Informe o número da nota fiscal abaixo para concluir o processo."
                + " Recebimento registrado por " + loggedUser.getUserName() + ".");
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        String nf = descriptionField != null ? descriptionField.getText().trim() : "";
        if (nf.isEmpty()) {
            if (errorLabel != null) errorLabel.setText("Informe o número da nota fiscal.");
            return;
        }

        try {
            // Monta o body com todos os itens do pedido
            ObjectNode body = mapper.createObjectNode();
            body.put("invoiceNumber", nf);
            body.put("invoiceSerie", "1");

            ArrayNode itemsArray = mapper.createArrayNode();
            for (RequestItem item : order.getItemsList()) {
                ObjectNode itemNode = mapper.createObjectNode();
                itemNode.put("productId",         item.getProduct().getId());
                itemNode.put("receivedQuantity",   item.getQuantity());
                itemsArray.add(itemNode);
            }
            body.set("items", itemsArray);

            ApiClient.post("/api/purchase-orders/" + order.getId() + "/receive",
                    mapper.writeValueAsString(body));

            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();

        } catch (Exception e) {
            if (errorLabel != null) errorLabel.setText("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}