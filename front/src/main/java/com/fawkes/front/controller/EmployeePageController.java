package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawkes.front.models.Employee;
import com.fawkes.front.service.ApiClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class EmployeePageController {

    private static final List<String> GRUPOS = Arrays.asList("DIRECTOR", "MANAGER", "OPERATIONAL");

    // Tabela
    @FXML private TableView<JsonNode>           employeeTable;
    @FXML private TableColumn<JsonNode, String> colId;
    @FXML private TableColumn<JsonNode, String> colNome;
    @FXML private TableColumn<JsonNode, String> colEmail;
    @FXML private TableColumn<JsonNode, String> colAtivo;
    @FXML private TableColumn<JsonNode, String> colGrupo;
    @FXML private TableColumn<JsonNode, String> colDepto;
    @FXML private TableColumn<JsonNode, String> colAcoes;

    // Barra superior
    @FXML private TextField searchField;
    @FXML private Label     statusLabel;

    // Dialog de cadastro
    @FXML private VBox          addDialog;
    @FXML private TextField     fieldUsername;
    @FXML private TextField     fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private ComboBox<String> fieldGroup;
    @FXML private TextField     fieldDept;
    @FXML private Label         addErrorLabel;

    private final ObservableList<JsonNode> allRows = FXCollections.observableArrayList();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("id").asText("-")));
        colNome.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("userName").asText("-")));
        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("userMail").asText("-")));
        colAtivo.setCellValueFactory(d -> {
            boolean active = d.getValue().path("isActive").asBoolean(true);
            return new SimpleStringProperty(active ? "Ativo" : "Inativo");
        });
        colGrupo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("groupName").asText("-")));
        colDepto.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().path("departamentName").asText("-")));

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

        loadEmployees();
    }

    @FXML
    public void loadEmployees() {
        statusLabel.setText("Carregando...");
        allRows.clear();
        try {
            JsonNode data = ApiClient.get("/api/users");
            for (JsonNode node : data) allRows.add(node);
            employeeTable.setItems(allRows);
            statusLabel.setText(allRows.size() + " funcionário(s) carregado(s).");
        } catch (Exception e) {
            statusLabel.setText("Erro ao carregar: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            employeeTable.setItems(allRows);
            return;
        }
        ObservableList<JsonNode> filtered = FXCollections.observableArrayList();
        for (JsonNode row : allRows) {
            if (row.path("userName").asText("").toLowerCase().contains(q)
                    || row.path("userMail").asText("").toLowerCase().contains(q)
                    || row.path("groupName").asText("").toLowerCase().contains(q)
                    || row.path("departamentName").asText("").toLowerCase().contains(q)) {
                filtered.add(row);
            }
        }
        employeeTable.setItems(filtered);
    }

    @FXML
    public void handleOpenAddDialog() {
        fieldUsername.clear();
        fieldEmail.clear();
        fieldPassword.clear();
        fieldGroup.getSelectionModel().clearSelection();
        fieldDept.clear();
        addErrorLabel.setText("");
        
        fieldGroup.setItems(FXCollections.observableArrayList(GRUPOS));
        
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
        String username = fieldUsername.getText().trim();
        String email    = fieldEmail.getText().trim();
        String password = fieldPassword.getText().trim();
        String group    = fieldGroup.getSelectionModel().getSelectedItem();
        String dept     = fieldDept.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                || group == null || dept.isEmpty()) {
            addErrorLabel.setText("Preencha todos os campos.");
            return;
        }

        try {
            String body = String.format(
                    "{\"userName\":\"%s\",\"userMail\":\"%s\",\"password\":\"%s\"," +
                            "\"isActive\":true,\"role\":\"%s\",\"departamentName\":\"%s\"}",
                    username, email, password, group, dept);

            ApiClient.post("/api/users", body);
            handleCloseAddDialog();
            loadEmployees();
        } catch (Exception e) {
            addErrorLabel.setText("Erro: " + e.getMessage());
        }
    }

    private void handleDelete(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja excluir este funcionário?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    ApiClient.delete("/api/users/" + id);
                    loadEmployees();
                } catch (Exception e) {
                    statusLabel.setText("Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }
}