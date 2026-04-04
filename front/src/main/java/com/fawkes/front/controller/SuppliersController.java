package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.SupplierCard;
import com.fawkes.front.models.Supplier;
import com.fawkes.front.service.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class SuppliersController {

    @FXML private FlowPane suppliersContainer;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        loadSuppliers();
    }

    @FXML
    public void loadSuppliers() {
        suppliersContainer.getChildren().clear();

        try {
            JsonNode data = ApiClient.get("/api/suppliers");

            if (!data.isArray() || data.isEmpty()) {
                statusLabel.setText("Nenhum fornecedor encontrado.");
                return;
            }

            for (JsonNode node : data) {
                Supplier supplier = Supplier.fromJson(node);
                SupplierCard card = new SupplierCard();
                card.setData(supplier);
                suppliersContainer.getChildren().add(card);
            }

            statusLabel.setText(data.size() + " fornecedor(es) carregado(s).");

        } catch (Exception e) {
            statusLabel.setText("Erro ao carregar fornecedores: " + e.getMessage());
        }
    }
}