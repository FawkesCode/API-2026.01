package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.jfoenix.controls.JFXButton;
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

    @FXML private TableView<JsonNode> supplierTable;
    @FXML private TableColumn<JsonNode, String> colId;
    @FXML private TableColumn<JsonNode, String> colNome;
    @FXML private TableColumn<JsonNode, String> colCnpj;
    @FXML private TableColumn<JsonNode, String> colPagamento;
    @FXML private TableColumn<JsonNode, String> colAcoes;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;
    @FXML private JFXButton btnNewSupplier;

    @FXML private VBox addDialog;
    @FXML private TextField fieldNome;
    @FXML private TextField fieldCnpj;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private Label addErrorLabel;

    private ObservableList<JsonNode> allItems = FXCollections.observableArrayList();

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

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Excluir");
            {
                btnDelete.setStyle("-fx-text-fill: #FF4A50;");
                btnDelete.setOnAction(e -> {
                    JsonNode item = getTableView().getItems().get(getIndex());
                    handleDelete(item.path("id").asLong());
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });

        comboPagamento.setItems(FXCollections.observableArrayList("PIX", "CREDITO", "DEBITO", "BOLETO"));
        comboPagamento.setValue("PIX");

        loadSuppliers();
    }

    @FXML
    public void loadSuppliers() {
        statusLabel.setText("Carregando...");
        new Thread(() -> {
            try {
                JsonNode data = ApiClient.get("/api/suppliers");
                javafx.application.Platform.runLater(() -> {
                    allItems.clear();
                    for (JsonNode item : data) allItems.add(item);
                    supplierTable.setItems(allItems);
                    statusLabel.setText("Fornecedores carregados. " + allItems.size() + " registro(s).");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Erro: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { supplierTable.setItems(allItems); return; }
        ObservableList<JsonNode> filtered = FXCollections.observableArrayList();
        for (JsonNode item : allItems) {
            if (item.path("nomeFornecedor").asText("").toLowerCase().contains(query) ||
                    item.path("cnpjFornecedor").asText("").toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }
        supplierTable.setItems(filtered);
    }

    @FXML
    public void handleOpenAddDialog() throws IOException {
        //fieldNome.clear(); fieldCnpj.clear();
        //comboPagamento.setValue("PIX");
        //addErrorLabel.setText("");
        //addDialog.setVisible(true);
        //addDialog.setManaged(true);
        Parent formulario = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/fawkes/front/view/forms/new-supplier-form.fxml")));
        Stage curStage = ((Stage) btnNewSupplier.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Fornecedor", 600, 400, "ModalFrameSM.fxml", true); // OBS: Esses são o mesmo parâmetro para o modal de visualizar item no estoque
    }

    @FXML
    public void handleCloseAddDialog() {
        addDialog.setVisible(false);
        addDialog.setManaged(false);
    }

    @FXML
    public void handleConfirmAdd() {
        String nome = fieldNome.getText().trim();
        String cnpj = fieldCnpj.getText().trim();
        String pagamento = comboPagamento.getValue();

        if (nome.isEmpty() || cnpj.isEmpty()) {
            addErrorLabel.setText("Preencha todos os campos.");
            return;
        }

        String body = String.format(
                "{\"nomeFornecedor\":\"%s\",\"cnpjFornecedor\":\"%s\",\"meioPagamento\":\"%s\"}",
                nome, cnpj, pagamento);

        new Thread(() -> {
            try {
                ApiClient.post("/api/suppliers", body);
                javafx.application.Platform.runLater(() -> {
                    handleCloseAddDialog();
                    loadSuppliers();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        addErrorLabel.setText("Erro: " + e.getMessage()));
            }
        }).start();
    }

    private void handleDelete(Long id) {
        new Thread(() -> {
            try {
                ApiClient.delete("/api/suppliers/" + id);
                javafx.application.Platform.runLater(this::loadSuppliers);
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Erro ao excluir: " + e.getMessage()));
            }
        }).start();
    }
}