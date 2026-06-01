package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.service.ApiClient;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuoteRequestForm {

    @FXML private VBox      itemsContainer;
    @FXML private Label     errorLabel;
    @FXML private JFXButton btnConfirm;
    @FXML private JFXButton btnCancel;

    private Order    order;
    private Runnable onSaveSuccess;

    private final List<long[]>    itemIds     = new ArrayList<>();
    private final List<TextField> priceFields = new ArrayList<>();

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    public void setData(Order order) {
        this.order = order;
        itemsContainer.getChildren().clear();
        itemIds.clear();
        priceFields.clear();

        for (RequestItem item : order.getItemsList()) {
            Label name = new Label(item.getProduct().getName());
            name.getStyleClass().add("input__label--info");
            name.setPrefWidth(220);

            Label qty = new Label("x " + item.getQuantity());
            qty.getStyleClass().add("input__label--info");

            StackPane spacer = new StackPane();
            spacer.setStyle("-fx-border-style: dotted; -fx-border-color: #818EA1; -fx-border-width: 0 0 3 0;");
            spacer.setMinHeight(5);
            spacer.setMaxHeight(5);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            double existingPrice = item.getUnitPrice();
            TextField priceField = new TextField(
                    existingPrice > 0
                            ? String.format("%.2f", existingPrice).replace(",", ".")
                            : "");
            priceField.setPromptText("Preço unitário (R$)");
            priceField.setPrefWidth(130);

            HBox row = new HBox(8, name, qty, spacer, priceField);
            row.setAlignment(Pos.CENTER_LEFT);
            itemsContainer.getChildren().add(row);

            itemIds.add(new long[]{item.getId()});
            priceFields.add(priceField);
        }
    }

    @FXML
    private void handleSubmit() {
        errorLabel.setText("");
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            ArrayNode items = mapper.createArrayNode();

            for (int i = 0; i < itemIds.size(); i++) {
                String raw = priceFields.get(i).getText().trim().replace(",", ".");
                if (raw.isEmpty()) {
                    errorLabel.setText("Preencha o preço de todos os itens.");
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(raw);
                } catch (NumberFormatException e) {
                    errorLabel.setText("Preço inválido: \"" + raw + "\".");
                    return;
                }
                if (price <= 0) {
                    errorLabel.setText("O preço deve ser maior que zero.");
                    return;
                }
                ObjectNode entry = mapper.createObjectNode();
                entry.put("itemId", itemIds.get(i)[0]);
                entry.put("unitPrice", price);
                items.add(entry);
            }

            body.set("items", items);
            ApiClient.put("/api/purchase-orders/" + order.getId() + "/items/prices",
                    mapper.writeValueAsString(body));

            if (onSaveSuccess != null) onSaveSuccess.run();
            ((Stage) btnConfirm.getScene().getWindow()).close();

        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
