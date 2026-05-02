package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.models.Employee;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
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

public class EditStockItemForm {
    @FXML
    private JFXButton btnClose;
    @FXML private JFXButton btnSubmit;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> suppilerField;
    @FXML private ComboBox<String> unityField;

    private StockItem product;
    private final Map<String, Long> supplierNameToId = new LinkedHashMap<>();
    private static final List<String> UNITIES = Arrays.asList("METROS", "CAIXAS", "LITROS", "KILOGRAMAS", "OUTROS", "NAO_DEFINIDO");
    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    public void setProductData(StockItem pro) {
        this.product = pro;
        nameField.setText(pro.getProductName());
        typeField.setText(pro.getProductType());
        priceField.setText(pro.getUnitValue().toString());
        unityField.getSelectionModel().select(pro.getMeasurementUnit());
        suppilerField.getSelectionModel().select(pro.getSupplierID());
    }

    @FXML
    public void initialize() {
        UnaryOperator<TextFormatter.Change> priceInput = change -> {
            String text = change.getControlNewText();

            // Regex for monetary values
            if(text.isEmpty() || text.matches("[1-9]\\d*(.\\d{0,2})?")) {
                return change;
            }

            return null;
        };

        priceField.setTextFormatter(new TextFormatter<>(priceInput));

        // SUPPLIERS COMBO BOX CONTENT
        loadSuppliers();

        unityField.getItems().addAll(UNITIES);

        unityField.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String unity) {
                return StringUtils.measureTranslation(unity);
            }

            @Override
            public String fromString(String s) {
                return null;
            }
        });

        unityField.setCellFactory(v -> new ListCell<String>() {
            @Override
            protected void  updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : StringUtils.measureTranslation(item));
            }
        });

        btnSubmit.setText("Salvar Alterações");

    }

    private void loadSuppliers() {
        try {
            JsonNode data = ApiClient.get("/api/suppliers");
            for (JsonNode node : data) {
                String name = node.path("supplierName").asText("Sem nome");
                Long id = node.path("id").asLong();
                supplierNameToId.put(name, id);
                suppilerField.getItems().add(name);
            }
        } catch (Exception e) {
            errorLabel.setText("Erro ao carregar fornecedores: " + e.getMessage());
        }
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
        if (nameField.getText().isEmpty() || priceField.getText().isEmpty() || unityField.getSelectionModel().getSelectedItem() == null || suppilerField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
            return;
        }

        String selectedSupplierName = suppilerField.getSelectionModel().getSelectedItem();
        Long supplierId = supplierNameToId.get(selectedSupplierName);
        String unity = unityField.getSelectionModel().getSelectedItem();

        String jsonBody = String.format(
            "{\"productName\":\"%s\",\"productType\":\"%s\",\"measurementUnit\":\"%s\",\"unitValue\":%s,\"supplierId\":%d}",
            nameField.getText(),
            typeField.getText(),
            unity,
            priceField.getText(),
            supplierId
        );

        try {
            ApiClient.put("/api/products/" + product.getProductId(), jsonBody);
            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }
            ((Stage) btnSubmit.getScene().getWindow()).close();
        } catch (Exception e) {
            errorLabel.setText("Erro ao salvar: " + e.getMessage());
        }
    }
}
