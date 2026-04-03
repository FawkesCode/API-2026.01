package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class EmployeePageController {

    @FXML private TableView<JsonNode> employeeTable;
    @FXML private TableColumn<JsonNode, String> colId;
    @FXML private TableColumn<JsonNode, String> colNome;
    @FXML private TableColumn<JsonNode, String> colEmail;
    @FXML private TableColumn<JsonNode, String> colAtivo;
    @FXML private TableColumn<JsonNode, String> colGrupo;
    @FXML private TableColumn<JsonNode, String> colDepto;
    @FXML private TableColumn<JsonNode, String> colAcoes;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;

    @FXML private VBox addDialog;
    @FXML private TextField fieldUsername;
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private TextField fieldGroupId;
    @FXML private TextField fieldDeptId;
    @FXML private Label addErrorLabel;

    private ObservableList<JsonNode> allItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("id").asText("-")));
        colNome.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("userName").asText("-")));
        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("userMail").asText("-")));
        colAtivo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("isActive").asBoolean() ? "Sim" : "Nao"));
        colGrupo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("group").path("id").asText("-")));
        colDepto.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("departments").path("id").asText("-")));

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

        loadEmployees();
    }

    @FXML
    public void loadEmployees() {
        statusLabel.setText("Carregando...");
        new Thread(() -> {
            try {
                JsonNode data = ApiClient.get("/api/users");
                javafx.application.Platform.runLater(() -> {
                    allItems.clear();
                    for (JsonNode item : data) allItems.add(item);
                    employeeTable.setItems(allItems);
                    statusLabel.setText("Funcionarios carregados. " + allItems.size() + " registro(s).");
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
        if (query.isEmpty()) { employeeTable.setItems(allItems); return; }
        ObservableList<JsonNode> filtered = FXCollections.observableArrayList();
        for (JsonNode item : allItems) {
            if (item.path("userName").asText("").toLowerCase().contains(query) ||
                    item.path("userMail").asText("").toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }
        employeeTable.setItems(filtered);
    }

    @FXML
    public void handleOpenAddDialog() throws IOException {
        //fieldUsername.clear(); fieldEmail.clear();
        //fieldPassword.clear(); fieldGroupId.clear(); fieldDeptId.clear();
        //addErrorLabel.setText("");
        //addDialog.setVisible(true);
        //addDialog.setManaged(true);
        Parent formulario = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/fawkes/front/view/forms/new-employee-form.fxml")));
        ModalManager.openModal(formulario, "Cadastrar Funcionário");
    }

    @FXML
    public void handleCloseAddDialog() {
        addDialog.setVisible(false);
        addDialog.setManaged(false);
    }

    @FXML
    public void handleConfirmAdd() {
        String username = fieldUsername.getText().trim();
        String email    = fieldEmail.getText().trim();
        String password = fieldPassword.getText().trim();
        String groupId  = fieldGroupId.getText().trim();
        String deptId   = fieldDeptId.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                || groupId.isEmpty() || deptId.isEmpty()) {
            addErrorLabel.setText("Preencha todos os campos.");
            return;
        }

        String body = String.format(
                "{\"userName\":\"%s\",\"userMail\":\"%s\",\"password\":\"%s\",\"isActive\":true,\"group\":{\"id\":%s},\"departments\":{\"id\":%s}}",
                username, email, password, groupId, deptId);

        new Thread(() -> {
            try {
                ApiClient.post("/api/users", body);
                javafx.application.Platform.runLater(() -> {
                    handleCloseAddDialog();
                    loadEmployees();
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
                ApiClient.delete("/api/users/" + id);
                javafx.application.Platform.runLater(this::loadEmployees);
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Erro ao excluir: " + e.getMessage()));
            }
        }).start();
    }
}