package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NewOrderFormController {

    @FXML private ComboBox<SupplierItem> supplierCombo;
    @FXML private ComboBox<String>       paymentCombo;
    @FXML private ComboBox<ProductItem>  productCombo;
    @FXML private TextField              quantityField;
    @FXML private TextArea               descriptionField;
    @FXML private Label                  errorLabel;

    private Runnable onSuccessCallback;

    // ObjectMapper garante JSON válido independente do Locale do SO
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }

    @FXML
    public void initialize() {
        loadSuppliers();
        loadProducts();

        supplierCombo.setOnAction(e -> {
            SupplierItem selected = supplierCombo.getValue();
            if (selected != null) {
                paymentCombo.setItems(FXCollections.observableArrayList(selected.paymentMethods));
                paymentCombo.getSelectionModel().selectFirst();
            }
        });
    }

    private void loadSuppliers() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/api/suppliers");
            }
        };
        task.setOnSucceeded(e -> {
            JsonNode data = task.getValue();
            List<SupplierItem> list = new ArrayList<>();
            if (data != null && data.isArray()) {
                for (JsonNode node : data) {
                    if (node.path("isActive").asBoolean(true)) {
                        Long id = node.path("id").asLong();
                        String name = node.path("supplierName").asText("Fornecedor");
                        List<String> methods = new ArrayList<>();
                        node.path("paymentMethods").forEach(m -> methods.add(m.asText()));
                        list.add(new SupplierItem(id, name, methods));
                    }
                }
            }
            Platform.runLater(() -> supplierCombo.setItems(FXCollections.observableArrayList(list)));
        });
        task.setOnFailed(e -> showError("Erro ao carregar fornecedores."));
        new Thread(task).start();
    }

    private void loadProducts() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/api/products");
            }
        };
        task.setOnSucceeded(e -> {
            JsonNode data = task.getValue();
            List<ProductItem> list = new ArrayList<>();
            if (data != null && data.isArray()) {
                for (JsonNode node : data) {
                    Long id = node.path("id").asLong();
                    String name = node.path("productName").asText("Produto");
                    double price = node.path("unitValue").asDouble(0.0);
                    list.add(new ProductItem(id, name, price));
                }
            }
            Platform.runLater(() -> productCombo.setItems(FXCollections.observableArrayList(list)));
        });
        task.setOnFailed(e -> showError("Erro ao carregar produtos."));
        new Thread(task).start();
    }

    @FXML
    public void handleOnSubmit(ActionEvent event) {
        clearError();

        SupplierItem supplier = supplierCombo.getValue();
        ProductItem  product  = productCombo.getValue();
        String       payment  = paymentCombo.getValue();
        String       qtyText  = quantityField.getText().trim();

        if (supplier == null)              { showError("Selecione um fornecedor."); return; }
        if (product  == null)              { showError("Selecione um produto.");    return; }
        if (payment  == null || payment.isBlank()) { showError("Selecione o método de pagamento."); return; }
        if (qtyText.isBlank())             { showError("Informe a quantidade.");    return; }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Quantidade deve ser um número inteiro maior que zero.");
            return;
        }

        BigDecimal unitPrice = BigDecimal.valueOf(product.unitPrice);
        String descricao = descriptionField.getText().trim();
        final int finalQuantity = quantity;

        showError("Enviando pedido...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String userId = UserInfoManager.getInstance().getUserId();

                // 1. Draft — ObjectMapper serializa Long corretamente
                ObjectNode draftNode = MAPPER.createObjectNode();
                draftNode.put("supplierId", supplier.id);
                draftNode.put("userId", Long.parseLong(userId));
                JsonNode draft = ApiClient.post("/api/purchase-orders/draft",
                        MAPPER.writeValueAsString(draftNode));
                Long orderId = draft.path("id").asLong();

                // 2. Item — unitPrice serializado como número (sem vírgula de locale)
                ObjectNode itemNode = MAPPER.createObjectNode();
                itemNode.put("productId", product.id);
                itemNode.put("quantity", finalQuantity);
                itemNode.put("unitPrice", unitPrice);
                if (!descricao.isBlank()) itemNode.put("notes", descricao);
                ApiClient.post("/api/purchase-orders/" + orderId + "/items",
                        MAPPER.writeValueAsString(itemNode));

                // 3. Submit → status vira "pending" (aparece na seção pendente)
                ApiClient.post("/api/purchase-orders/" + orderId + "/submit", "{}");

                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            closeStage();
            if (onSuccessCallback != null) onSuccessCallback.run();
        }));

        task.setOnFailed(e -> Platform.runLater(() ->
                showError("Erro: " + task.getException().getMessage())));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    public void closeModal(ActionEvent event) { closeStage(); }

    private void closeStage() {
        if (errorLabel.getScene() != null)
            ((Stage) errorLabel.getScene().getWindow()).close();
    }

    private void showError(String msg) { Platform.runLater(() -> errorLabel.setText(msg)); }
    private void clearError()          { errorLabel.setText(""); }

    // Inner classes

    public static class SupplierItem {
        final Long id;
        final String name;
        final List<String> paymentMethods;

        public SupplierItem(Long id, String name, List<String> paymentMethods) {
            this.id = id; this.name = name; this.paymentMethods = paymentMethods;
        }
        @Override public String toString() { return name; }
    }

    public static class ProductItem {
        final Long id;
        final String name;
        final double unitPrice;

        public ProductItem(Long id, String name, double unitPrice) {
            this.id = id; this.name = name; this.unitPrice = unitPrice;
        }
        @Override public String toString() { return name; }
    }
}