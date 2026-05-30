package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import static java.lang.Integer.parseInt;

public class ExitStockItemForm {
    @FXML private Button btnClose;
    @FXML private Label errorLabel;

    @FXML private TextField qtdField;
    @FXML private ComboBox<String> productField;

    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    // Guarda productId E stockId para cada produto
    private final Map<String, Long> PRODUCT_IDS  = new LinkedHashMap<>();
    private final Map<String, Long> STOCK_IDS    = new LinkedHashMap<>();

    private void loadProducts() {
        new Thread(() -> {
            try {
                JsonNode productData = ApiClient.listStock();
                ObservableList<String> productItems = FXCollections.observableArrayList();
                PRODUCT_IDS.clear();
                STOCK_IDS.clear();

                for (JsonNode product : productData) {
                    Long productId = product.path("productId").asLong();
                    Long stockId   = product.path("stockId").asLong();   // pega o stockId real
                    String name    = product.path("productName").asText("Produto " + productId);

                    productItems.add(name);
                    PRODUCT_IDS.put(name, productId);
                    STOCK_IDS.put(name, stockId);
                }

                javafx.application.Platform.runLater(() -> {
                    productField.setItems(productItems);
                });
            } catch (Exception e) {
                System.err.println("Erro ao carregar produtos: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    public void initialize() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.isEmpty() || text.matches("[1-9]\\d*")) return change;
            return null;
        };
        qtdField.setTextFormatter(new TextFormatter<>(filter));
        loadProducts();
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
        if (qtdField.getText().isEmpty() || productField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verifique se todos os campos obrigatórios foram preenchidos.");
            return;
        }
        errorLabel.setText("");

        String selectedProduct = productField.getSelectionModel().getSelectedItem();
        Integer qty       = parseInt(qtdField.getText().trim());
        Long productId    = PRODUCT_IDS.get(selectedProduct);
        Long stockId      = STOCK_IDS.get(selectedProduct);  // stockId real do produto

        if (stockId == null || stockId == 0) {
            errorLabel.setText("Produto sem estoque vinculado. Contate o administrador.");
            return;
        }

        try {
            ApiClient.registerOutput(stockId, productId, qty);

            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}