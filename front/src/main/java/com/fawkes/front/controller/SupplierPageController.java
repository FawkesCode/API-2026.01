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
import java.util.Objects;

public class SupplierPageController {

    // Tabela
    @FXML private TableView<JsonNode>           supplierTable;
    @FXML private TableColumn<JsonNode, String> colId;
    @FXML private TableColumn<JsonNode, String> colNome;
    @FXML private TableColumn<JsonNode, String> colCnpj;
    @FXML private TableColumn<JsonNode, String> colPagamento;
    @FXML private TableColumn<JsonNode, String> colAcoes;

    // Barra superior
    @FXML private TextField searchField;
    @FXML private Label     statusLabel;
    @FXML private Button    btnNewSupplier;

    // Dialog de cadastro
    @FXML private VBox      addDialog;
    @FXML private TextField fieldNome;
    @FXML private TextField fieldCnpj;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private Label     addErrorLabel;

    private final ObservableList<JsonNode> allRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Apply RBAC restrictions based on user role
        applyRBACRestrictions();

        colId.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("id").asText("-")));
        colNome.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("nomeFornecedor").asText("-")));
        colCnpj.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("cnpjFornecedor").asText("-")));
        colPagamento.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("meioPagamento").asText("-")));

        // Coluna de ações: botão Excluir
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Excluir");
            {
                btnDelete.setStyle("-fx-text-fill: #FF4A50; -fx-cursor: hand;");
                btnDelete.setOnAction(e -> {
                    JsonNode row = getTableView().getItems().get(getIndex());
                    handleDelete(row.path("id").asLong());
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });

        loadSuppliers();
    }

    private void applyRBACRestrictions() {
        // Only DIRECTOR and MANAGER can create new suppliers
        if (!RBACUtil.canManageSuppliers()) {
            btnNewSupplier.setVisible(false);
            btnNewSupplier.setManaged(false);
        }
    }

    @FXML
    public void loadSuppliers() {
        statusLabel.setText("Carregando...");
        allRows.clear();
        try {
            JsonNode data = ApiClient.get("/api/suppliers");
            for (JsonNode node : data) allRows.add(node);
            supplierTable.setItems(allRows);
            statusLabel.setText(allRows.size() + " fornecedor(es) carregado(s).");
        } catch (Exception e) {
            statusLabel.setText("Erro ao carregar: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            supplierTable.setItems(allRows);
            return;
        }
        ObservableList<JsonNode> filtered = FXCollections.observableArrayList();
        for (JsonNode row : allRows) {
            if (row.path("nomeFornecedor").asText("").toLowerCase().contains(q)
                    || row.path("cnpjFornecedor").asText("").toLowerCase().contains(q)
                    || row.path("meioPagamento").asText("").toLowerCase().contains(q)) {
                filtered.add(row);
            }
        }
        supplierTable.setItems(filtered);
    }

    @FXML
    public void handleOpenAddDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-supplier-form.fxml"));
        Parent formulario = loader.load();
        AddSupplierForm controller = loader.getController();
        controller.setOnSaveSuccess(this::loadSuppliers);
        Stage curStage = ((Stage) btnNewSupplier.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Fornecedor", 600, 400, "ModalFrameSM.fxml", true);
    }

    private void handleDelete(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja excluir este fornecedor?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    ApiClient.delete("/api/suppliers/" + id);
                    loadSuppliers();
                } catch (Exception e) {
                    statusLabel.setText("Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }
}