package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static java.lang.Integer.parseInt;

public class ExitStockItemForm {
    @FXML private Button btnClose;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private TextField qtdField;
    @FXML private ComboBox<String> productField;
    private static final Long DEFAULT_STOCK_ID = 1L;

    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    private final Map<String, Long> PRODUCTS = new LinkedHashMap<>();

    private void loadProducts() {
        new Thread(() -> {
            try {
                JsonNode productData = ApiClient.listStock();
                ObservableList<String> productItems = FXCollections.observableArrayList();
                PRODUCTS.clear();

                for (JsonNode product : productData) {
                    Long id = product.path("productId").asLong();
                    String name = product.path("productName").asText("Produto " + id);
                    productItems.add(name);
                    PRODUCTS.put(name, id);
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

            if(text.isEmpty() || text.matches("[1-9]\\d*")) {
                return change;
            }

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
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
            return;
        }
        errorLabel.setText("");

        String selectedProduct = productField.getSelectionModel().getSelectedItem();
        Integer qty = parseInt(qtdField.getText().trim());
        Long productId = PRODUCTS.get(selectedProduct);

        try {
            // Usa o estoque padrão da empresa
            ApiClient.registerOutput(DEFAULT_STOCK_ID, productId, qty);

            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            handleCloseModal();
        } catch (Exception e) {
            errorLabel.setText( "Erro: " + e.getMessage());
        }

    }
}
