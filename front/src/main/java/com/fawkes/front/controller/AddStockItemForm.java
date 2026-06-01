package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.File;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class AddStockItemForm {
    @FXML private JFXButton btnClose;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private ComboBox<String> suppilerField;
    @FXML private ComboBox<String> unityField;

    private final Map<String, Long> supplierNameToId = new LinkedHashMap<>();
    private static final List<String> UNITIES = Arrays.asList("METROS", "CAIXAS", "LITROS", "KILOGRAMAS", "OUTROS", "NAO_DEFINIDO");
    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    @FXML
    public void initialize() {
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
    private void handleOnSubmit(ActionEvent event) {
        if (nameField.getText().isEmpty() || suppilerField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
            return;
        }

        try {
            String selectedSupplier = suppilerField.getSelectionModel().getSelectedItem();
            Long supplierId = supplierNameToId.get(selectedSupplier);

            String body = String.format(
                    "{\"productName\":\"%s\",\"productType\":\"%s\",\"measurementUnit\":\"%s\"," +
                            "\"unitValue\":\"\",\"description\":\"\",\"supplierId\":%d,\"stockId\":1}",
                    nameField.getText().trim(),
                    typeField.getText().trim(),
                    unityField.getSelectionModel().getSelectedItem(),
                    supplierId
            );

            JsonNode response = ApiClient.post("/api/products", body);
            System.out.println("RETORNO DO BACKEND: " + response.toPrettyString());

            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            ((Stage) btnClose.getScene().getWindow()).close();
        } catch (Exception e) {
            errorLabel.setText("Erro ao cadastrar produto: " + e.getMessage());
        }
    }
}
