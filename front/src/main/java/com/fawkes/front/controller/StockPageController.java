package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.RBACUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

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
    @FXML private Button btnInput;
    @FXML private Button btnOutput;

    @FXML private VBox      inputDialog;
    @FXML private TextField inputStockId;
    @FXML private TextField inputProductId;
    @FXML private TextField inputQuantity;
    @FXML private Label     inputErrorLabel;

    // --- Diálogo de Saída ---
    @FXML private VBox      outputDialog;
    @FXML private ComboBox<String> outputProductCombo;
    @FXML private TextField outputQuantity;
    @FXML private Label     outputErrorLabel;

    private ObservableList<JsonNode> allItems = FXCollections.observableArrayList();
    private HashMap<String, Long> productMap = new HashMap<>();
    private static final Long DEFAULT_STOCK_ID = 1L; // Estoque padrão da empresa

    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    public void initialize() {
        // Apply RBAC restrictions based on user role
        applyRBACRestrictions();

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

        /*stockTable.setRowFactory(tv -> new TableRow<>() {
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
        });*/

        loadStock();
    }

    private void applyRBACRestrictions() {
        // OPERATIONAL users cannot create new products, only register output
        if (!RBACUtil.canManageProducts()) {
            btnInput.setVisible(false);
            btnInput.setManaged(false);
        }
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
    public void handleOpenInputDialog() throws IOException {
        //clearDialog(inputStockId, inputProductId, inputQuantity, inputErrorLabel);
        //outputDialog.setVisible(false);
        //outputDialog.setManaged(false);
        //inputDialog.setVisible(true);
        //inputDialog.setManaged(true);
        Parent formulario = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/fawkes/front/view/forms/new-stockItem-form.fxml")));
        Stage curStage = ((Stage) btnInput.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Produto");
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
        clearOutputDialog();
        loadProducts();
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
        String selectedProduct = outputProductCombo.getSelectionModel().getSelectedItem();
        String qtyText = outputQuantity.getText().trim();

        // Validação: verificar se seleção foi feita
        if (selectedProduct == null || selectedProduct.isEmpty()) {
            setErrorStyle(outputErrorLabel, "Por favor, selecione um produto.");
            return;
        }

        Integer qty = parseInt(qtyText, "Quantidade", outputErrorLabel);
        if (qty == null) return;

        Long productId = productMap.get(selectedProduct);

        try {
            // Usa o estoque padrão da empresa
            ApiClient.registerOutput(DEFAULT_STOCK_ID, productId, qty);
            setSuccessStyle(outputErrorLabel, "Saída registrada com sucesso!");
            loadStock();
            handleCloseOutputDialog();
        } catch (Exception e) {
            setErrorStyle(outputErrorLabel, "Erro: " + e.getMessage());
        }
    }

    /**
     * Carrega produtos do API e popula o ComboBox
     */
    private void loadProducts() {
        new Thread(() -> {
            try {
                JsonNode productData = ApiClient.listStock();
                ObservableList<String> productItems = FXCollections.observableArrayList();
                productMap.clear();

                for (JsonNode product : productData) {
                    Long id = product.path("productId").asLong();
                    String name = product.path("productName").asText("Produto " + id);
                    String displayName = name + " (ID: " + id + ")";
                    productItems.add(displayName);
                    productMap.put(displayName, id);
                }

                // Atualizar UI na thread principal
                javafx.application.Platform.runLater(() -> {
                    outputProductCombo.setItems(productItems);
                });
            } catch (Exception e) {
                System.err.println("Erro ao carregar produtos: " + e.getMessage());
            }
        }).start();
    }

    private void clearOutputDialog() {
        outputProductCombo.getSelectionModel().clearSelection();
        outputQuantity.clear();
        outputErrorLabel.setText("");
        outputErrorLabel.setStyle("-fx-text-fill: #FF4A50;");
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
        if (text == null || text.trim().isEmpty()) {
            setErrorStyle(err, fieldName + " é obrigatório.");
            return null;
        }
        try {
            int v = Integer.parseInt(text.trim());
            if (v <= 0) throw new NumberFormatException();
            err.setText("");
            return v;
        } catch (NumberFormatException e) {
            setErrorStyle(err, fieldName + " deve ser um número inteiro positivo.");
            return null;
        }
    }

    private void setErrorStyle(Label label, String message) {
        label.setStyle("-fx-text-fill: #FF4A50;");
        label.setText(message);
    }

    private void setSuccessStyle(Label label, String message) {
        label.setStyle("-fx-text-fill: #2e7d32;");
        label.setText(message);
    }
}
