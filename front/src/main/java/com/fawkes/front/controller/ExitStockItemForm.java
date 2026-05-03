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

    @FXML private JFXButton btnClose;
    @FXML private Label errorLabel;

    @FXML private TextField qtdField;
    @FXML private ComboBox<String> productField;

    private Runnable onSaveSuccess;

    // Mapa produto nome → productId
    private final Map<String, Long> PRODUCTS = new LinkedHashMap<>();
    // Mapa produto nome → stockId (cada produto pertence a um estoque)
    private final Map<String, Long> STOCK_IDS = new LinkedHashMap<>();

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    private void loadProducts() {
        new Thread(() -> {
            try {
                // /api/stock retorna StockItemDTO que tem productId
                // mas não tem stockId diretamente — buscamos de /api/products
                JsonNode stockData = ApiClient.listStock();
                JsonNode productData = ApiClient.get("/api/products");

                // Monta mapa productId → stockId a partir da lista completa de produtos
                Map<Long, Long> productToStock = new LinkedHashMap<>();
                if (productData != null && productData.isArray()) {
                    for (JsonNode p : productData) {
                        Long pid  = p.path("id").asLong();
                        Long sid  = p.path("stock").path("id").asLong(-1L);
                        if (sid > 0) productToStock.put(pid, sid);
                    }
                }

                ObservableList<String> productItems = FXCollections.observableArrayList();
                PRODUCTS.clear();
                STOCK_IDS.clear();

                if (stockData != null && stockData.isArray()) {
                    for (JsonNode item : stockData) {
                        Long productId = item.path("productId").asLong();
                        String name    = item.path("productName").asText("Produto " + productId);
                        productItems.add(name);
                        PRODUCTS.put(name, productId);

                        // Pega o stockId do mapa, ou usa -1 como fallback
                        Long stockId = productToStock.getOrDefault(productId, -1L);
                        STOCK_IDS.put(name, stockId);
                    }
                }

                javafx.application.Platform.runLater(() -> productField.setItems(productItems));

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
        String selected = productField.getSelectionModel().getSelectedItem();

        if (qtdField.getText().isEmpty() || selected == null) {
            errorLabel.setText("Verifique se todos os campos obrigatórios foram preenchidos.");
            return;
        }

        errorLabel.setText("");

        Long productId = PRODUCTS.get(selected);
        Long stockId   = STOCK_IDS.get(selected);

        if (stockId == null || stockId <= 0) {
            errorLabel.setText("Erro: produto sem estoque associado. Contate o suporte.");
            return;
        }

        Integer qty = parseInt(qtdField.getText().trim());

        try {
            ApiClient.registerOutput(stockId, productId, qty);
            if (onSaveSuccess != null) onSaveSuccess.run();
            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText("Erro: " + e.getMessage());
        }
    }
}