package com.fawkes.front.controller;

// Substitui: src/main/java/com/fawkes/front/controller/AddStockItemForm.java

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

public class AddStockItemForm {

    @FXML private JFXButton btnClose;
    @FXML private Label errorLabel;

    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> suppilerField;
    @FXML private ComboBox<String> unityField;

    private final Map<String, Long> supplierNameToId = new LinkedHashMap<>();
    private static final List<String> UNITIES = Arrays.asList(
            "METROS", "CAIXAS", "LITROS", "KILOGRAMAS", "OUTROS", "NAO_DEFINIDO");
    private Runnable onSaveSuccess;

    // ObjectMapper garante JSON válido independente do Locale do SO
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ID do estoque, carregado dinamicamente no initialize()
    private Long stockId = null;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    @FXML
    public void initialize() {
        UnaryOperator<TextFormatter.Change> priceInput = change -> {
            String text = change.getControlNewText();
            if (text.isEmpty() || text.matches("\\d+(\\.\\d{0,2})?")) return change;
            return null;
        };
        priceField.setTextFormatter(new TextFormatter<>(priceInput));

        loadSuppliers();
        loadStockId();

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

    // Busca o ID real do estoque principal em vez de usar 1 fixo
    private void loadStockId() {
        try {
            JsonNode data = ApiClient.get("/api/stock/first");
            this.stockId = data.path("id").asLong(-1L);
        } catch (Exception e) {
            // Fallback: tenta pegar da lista de produtos existentes
            try {
                JsonNode products = ApiClient.get("/api/products");
                if (products.isArray() && products.size() > 0) {
                    this.stockId = products.get(0).path("stock").path("id").asLong(-1L);
                }
            } catch (Exception ex) {
                errorLabel.setText("Aviso: não foi possível carregar o estoque.");
            }
        }
    }

    @FXML
    private void closeModal(ActionEvent event) {
        ((Stage) btnClose.getScene().getWindow()).close();
    }

    @FXML
    private void handleOnSubmit(ActionEvent event) {
        if (nameField.getText().isEmpty()
                || priceField.getText().isEmpty()
                || suppilerField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verifique se todos os campos obrigatórios foram preenchidos.");
            return;
        }

        if (stockId == null || stockId <= 0) {
            errorLabel.setText("Erro: estoque não encontrado. Reinicie o sistema.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceField.getText().trim());
        } catch (NumberFormatException e) {
            errorLabel.setText("Valor inválido. Use ponto como separador decimal (ex: 29.90).");
            return;
        }

        try {
            String selectedSupplier = suppilerField.getSelectionModel().getSelectedItem();
            Long supplierId = supplierNameToId.get(selectedSupplier);
            String unity = unityField.getSelectionModel().getSelectedItem();

            // ObjectMapper serializa corretamente números (sem vírgula de locale)
            ObjectNode body = MAPPER.createObjectNode();
            body.put("productName",     nameField.getText().trim());
            body.put("productType",     typeField.getText().trim());
            body.put("measurementUnit", unity != null ? unity : "NAO_DEFINIDO");
            body.put("unitValue",       price);
            body.put("description",     "");
            body.put("supplierId",      supplierId);
            body.put("stockId",         stockId); // ID real do banco

            JsonNode response = ApiClient.post("/api/products", MAPPER.writeValueAsString(body));
            System.out.println("Produto cadastrado: " + response.path("id").asText());

            if (onSaveSuccess != null) onSaveSuccess.run();
            ((Stage) btnClose.getScene().getWindow()).close();

        } catch (Exception e) {
            errorLabel.setText("Erro ao cadastrar produto: " + e.getMessage());
        }
    }
}