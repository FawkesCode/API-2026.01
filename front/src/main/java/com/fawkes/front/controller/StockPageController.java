package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

public class StockPageController {

    @FXML private TableView<JsonNode>          stockTable;
    @FXML private TableColumn<JsonNode, String> colName;
    @FXML private TableColumn<JsonNode, String> colType;
    @FXML private TableColumn<JsonNode, String> colUnit;
    @FXML private TableColumn<JsonNode, String> colValue;
    @FXML private TableColumn<JsonNode, String> colCurrent;
    @FXML private TableColumn<JsonNode, String> colMin;
    @FXML private TableColumn<JsonNode, String> colMax;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;

    @FXML private VBox      inputDialog;
    @FXML private TextField inputStockId;
    @FXML private TextField inputProductId;
    @FXML private TextField inputQuantity;
    @FXML private Label     inputErrorLabel;

    // --- Diálogo de Saída ---
    @FXML private VBox      outputDialog;
    @FXML private TextField outputStockId;
    @FXML private TextField outputProductId;
    @FXML private TextField outputQuantity;
    @FXML private Label     outputErrorLabel;

    private ObservableList<JsonNode> allItems = FXCollections.observableArrayList();

    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    public void initialize() {
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("productName").asText("-")));
        colType.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("productType").asText("-")));
        colUnit.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("measurementUnit").asText("-")));
        colValue.setCellValueFactory(d -> {
            double v = d.getValue().path("unitValue").asDouble(0);
            return new SimpleStringProperty(CURRENCY_FMT.format(v));
        });
        colCurrent.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("currentStockQuantity").asText("-")));
        colMin.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("minStockQuantity").asText("-")));
        colMax.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("maxStockQuantity").asText("-")));

        stockTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(JsonNode item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    int current = item.path("currentStockQuantity").asInt(Integer.MAX_VALUE);
                    int min     = item.path("minStockQuantity").asInt(0);
                    setStyle(current <= min ? "-fx-background-color: #fff0f0;" : "");
                }
            }
        });

        loadStock();
    }

    @FXML
    public void loadStock() {
        statusLabel.setText("Carregando...");
        try {
            JsonNode data = ApiClient.listStock();
            allItems.clear();
            for (JsonNode item : data) {
                allItems.add(item);
            }
            stockTable.setItems(allItems);
            statusLabel.setText("Estoque carregado. " + allItems.size() + " item(ns).");
        } catch (Exception e) {
            statusLabel.setText("Erro ao carregar estoque: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            stockTable.setItems(allItems);
            return;
        }
        ObservableList<JsonNode> filtered = FXCollections.observableArrayList();
        for (JsonNode item : allItems) {
            if (item.path("productName").asText("").toLowerCase().contains(query) ||
                    item.path("productType").asText("").toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }
        stockTable.setItems(filtered);
    }

    @FXML
    public void handleOpenInputDialog() {
        clearDialog(inputStockId, inputProductId, inputQuantity, inputErrorLabel);
        outputDialog.setVisible(false);
        outputDialog.setManaged(false);
        inputDialog.setVisible(true);
        inputDialog.setManaged(true);
    }

    @FXML
    public void handleCloseInputDialog() {
        inputDialog.setVisible(false);
        inputDialog.setManaged(false);
    }

    @FXML
    public void handleConfirmInput() {
        Long stockId   = parseLong(inputStockId.getText(), "ID do Estoque", inputErrorLabel);
        Long productId = parseLong(inputProductId.getText(), "ID do Produto", inputErrorLabel);
        Integer qty    = parseInt(inputQuantity.getText(), "Quantidade", inputErrorLabel);
        if (stockId == null || productId == null || qty == null) return;

        try {
            ApiClient.registerInput(stockId, productId, qty);
            inputErrorLabel.setStyle("-fx-text-fill: #2e7d32;");
            inputErrorLabel.setText("Entrada registrada com sucesso!");
            loadStock();
        } catch (Exception e) {
            inputErrorLabel.setStyle("-fx-text-fill: #FF4A50;");
            inputErrorLabel.setText("Erro: " + e.getMessage());
        }
    }

    @FXML
    public void handleOpenOutputDialog() {
        clearDialog(outputStockId, outputProductId, outputQuantity, outputErrorLabel);
        inputDialog.setVisible(false);
        inputDialog.setManaged(false);
        outputDialog.setVisible(true);
        outputDialog.setManaged(true);
    }

    @FXML
    public void handleCloseOutputDialog() {
        outputDialog.setVisible(false);
        outputDialog.setManaged(false);
    }

    @FXML
    public void handleConfirmOutput() {
        Long stockId   = parseLong(outputStockId.getText(), "ID do Estoque", outputErrorLabel);
        Long productId = parseLong(outputProductId.getText(), "ID do Produto", outputErrorLabel);
        Integer qty    = parseInt(outputQuantity.getText(), "Quantidade", outputErrorLabel);
        if (stockId == null || productId == null || qty == null) return;

        try {
            ApiClient.registerOutput(stockId, productId, qty);
            outputErrorLabel.setStyle("-fx-text-fill: #2e7d32;");
            outputErrorLabel.setText("Saída registrada com sucesso!");
            loadStock();
        } catch (Exception e) {
            outputErrorLabel.setStyle("-fx-text-fill: #FF4A50;");
            outputErrorLabel.setText("Erro: " + e.getMessage());
        }
    }

    private void clearDialog(TextField f1, TextField f2, TextField f3, Label err) {
        f1.clear(); f2.clear(); f3.clear();
        err.setText("");
        err.setStyle("-fx-text-fill: #FF4A50;");
    }

    private Long parseLong(String text, String fieldName, Label err) {
        try {
            long v = Long.parseLong(text.trim());
            if (v <= 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            err.setStyle("-fx-text-fill: #FF4A50;");
            err.setText(fieldName + " deve ser um número inteiro positivo.");
            return null;
        }
    }

    private Integer parseInt(String text, String fieldName, Label err) {
        try {
            int v = Integer.parseInt(text.trim());
            if (v <= 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            err.setStyle("-fx-text-fill: #FF4A50;");
            err.setText(fieldName + " deve ser um número inteiro positivo.");
            return null;
        }
    }
}