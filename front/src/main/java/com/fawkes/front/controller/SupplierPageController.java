package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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

    // Dialog de cadastro
    @FXML private VBox      addDialog;
    @FXML private TextField fieldNome;
    @FXML private TextField fieldCnpj;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private Label     addErrorLabel;

    private final ObservableList<JsonNode> allRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
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

        // Preenche o ComboBox com as opções do enum do back
        comboPagamento.setItems(FXCollections.observableArrayList(
                "PIX", "CREDITO", "DEBITO", "BOLETO"));
        comboPagamento.getSelectionModel().selectFirst();

        loadSuppliers();
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
    public void handleOpenAddDialog() {
        fieldNome.clear();
        fieldCnpj.clear();
        comboPagamento.getSelectionModel().selectFirst();
        addErrorLabel.setText("");
        addDialog.setVisible(true);
        addDialog.setManaged(true);
    }

    @FXML
    public void handleCloseAddDialog() {
        addDialog.setVisible(false);
        addDialog.setManaged(false);
    }

    @FXML
    public void handleConfirmAdd() {
        String nome      = fieldNome.getText().trim();
        String cnpj      = fieldCnpj.getText().trim();
        String pagamento = comboPagamento.getValue();

        if (nome.isEmpty() || cnpj.isEmpty()) {
            addErrorLabel.setText("Preencha nome e CNPJ.");
            return;
        }

        try {
            String body = String.format(
                    "{\"nomeFornecedor\":\"%s\",\"cnpjFornecedor\":\"%s\",\"meioPagamento\":\"%s\"}",
                    nome, cnpj, pagamento);
            ApiClient.post("/api/suppliers", body);
            handleCloseAddDialog();
            loadSuppliers();
        } catch (Exception e) {
            addErrorLabel.setText("Erro: " + e.getMessage());
        }
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