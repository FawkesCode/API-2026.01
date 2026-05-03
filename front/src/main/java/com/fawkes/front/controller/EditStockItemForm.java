package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class EditStockItemForm {

    @FXML private JFXButton btnClose;
    @FXML private JFXButton btnSubmit;
    @FXML private Label errorLabel;

    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> suppilerField;
    @FXML private ComboBox<String> unityField;

    private StockItem product;
    private final Map<String, Long> supplierNameToId = new LinkedHashMap<>();
    private static final List<String> UNITIES = Arrays.asList(
            "METROS", "CAIXAS", "LITROS", "KILOGRAMAS", "OUTROS", "NAO_DEFINIDO");
    private Runnable onSaveSuccess;

    // ObjectMapper garante JSON válido independente do Locale do SO
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    public void setProductData(StockItem pro) {
        this.product = pro;
        nameField.setText(pro.getProductName());
        typeField.setText(pro.getProductType());
        // Usa toPlainString() para evitar notação científica
        priceField.setText(pro.getUnitValue().toPlainString());
        unityField.getSelectionModel().select(pro.getMeasurementUnit());

        // Seleciona o fornecedor pelo nome (aguarda loadSuppliers terminar)
        // A seleção acontece no loadSuppliers após popular o combo
    }

    @FXML
    public void initialize() {
        // Filtro: aceita apenas números e ponto decimal
        UnaryOperator<TextFormatter.Change> priceInput = change -> {
            String text = change.getControlNewText();
            if (text.isEmpty() || text.matches("\\d+(\\.\\d{0,2})?")) return change;
            return null;
        };
        priceField.setTextFormatter(new TextFormatter<>(priceInput));

        loadSuppliers();

        unityField.getItems().addAll(UNITIES);

        unityField.setConverter(new StringConverter<>() {
            @Override public String toString(String unity) {
                return StringUtils.measureTranslation(unity);
            }
            @Override public String fromString(String s) { return null; }
        });

        unityField.setCellFactory(v -> new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
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

            // Seleciona o fornecedor atual do produto pelo nome
            if (product != null && product.getSupplierName() != null) {
                suppilerField.getSelectionModel().select(product.getSupplierName());
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
        if (nameField.getText().isEmpty()
                || priceField.getText().isEmpty()
                || unityField.getSelectionModel().getSelectedItem() == null
                || suppilerField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verifique se todos os campos obrigatórios foram preenchidos.");
            return;
        }

        String selectedSupplierName = suppilerField.getSelectionModel().getSelectedItem();
        Long supplierId = supplierNameToId.get(selectedSupplierName);
        String unity    = unityField.getSelectionModel().getSelectedItem();

        BigDecimal price;
        try {
            price = new BigDecimal(priceField.getText().trim());
        } catch (NumberFormatException e) {
            errorLabel.setText("Valor inválido. Use ponto como separador decimal (ex: 29.90).");
            return;
        }

        try {
            // ObjectMapper garante que unitValue sai como número (29.9),
            // nunca com vírgula (29,9) independente do Locale do Windows
            ObjectNode body = MAPPER.createObjectNode();
            body.put("productName",     nameField.getText().trim());
            body.put("productType",     typeField.getText().trim());
            body.put("measurementUnit", unity);
            body.put("unitValue",       price);
            body.put("supplierId",      supplierId); // Long → serializado corretamente

            ApiClient.put("/api/products/" + product.getProductId(),
                    MAPPER.writeValueAsString(body));

            if (onSaveSuccess != null) onSaveSuccess.run();
            ((Stage) btnSubmit.getScene().getWindow()).close();

        } catch (Exception e) {
            errorLabel.setText("Erro ao salvar: " + e.getMessage());
        }
    }
}