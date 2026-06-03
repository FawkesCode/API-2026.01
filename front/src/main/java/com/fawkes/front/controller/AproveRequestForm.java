package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AproveRequestForm {
    @FXML private Label detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private Label deliveryLabel;
    @FXML private Label errorLabel;
    UserInfoManager loggedUser = UserInfoManager.getInstance();

    private Runnable onSaveSuccess;
    public void setOnSaveSuccess(Runnable onSaveSuccess) { this.onSaveSuccess = onSaveSuccess; }
    private Order order;

    public void initialize() {
        btnCommand.setText("Marcar como Comprado");
        deliveryDatePicker.setValue(LocalDate.now().plusDays(3));
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

        String text = "Pedido de " + order.getQuantity() + " itens, sendo eles: " + productsList
                + "; Total de " + order.getTotalValue() + ". Compra pedida pelo " + order.getRequesterName()
                + " para o setor " + order.getSector().toUpperCase()
                + ". Marcando como comprado por " + loggedUser.getUserName() + ".";

        detailsLabel.setText(text);
        this.order = order;
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }

    @FXML
    private void handleSubmit() {
        try {
            LocalDate date = deliveryDatePicker.getValue();
            if (date == null) {
                errorLabel.setText("Informe a data de entrega prevista.");
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            // fim do dia, para não marcar "em atraso" no próprio dia da entrega
            body.put("expectedDeliveryDate", date.atTime(23, 59, 59).toString());
            String reason = descriptionField.getText();
            if (reason != null && !reason.isBlank()) body.put("reason", reason.trim());

            JsonNode response = ApiClient.post("/api/purchase-orders/" + order.getId() + "/confirm",
                    mapper.writeValueAsString(body));
            System.out.println("RETORNO DO BACKEND: " + response.toPrettyString());

            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}
