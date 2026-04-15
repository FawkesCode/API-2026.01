package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawkes.front.models.Employee;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    @FXML private Button    newEmployee;

    // Dialog de cadastro
    //@FXML private VBox          addDialog;
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
        // Apply RBAC restrictions based on user role
        applyRBACRestrictions();

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

    private void applyRBACRestrictions() {
        // Only DIRECTOR and MANAGER can create new employees
        if (!RBACUtil.canManageEmployees()) {
            newEmployee.setVisible(false);
            newEmployee.setManaged(false);
        }
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
    public void handleOpenAddDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-employee-form.fxml"));
        Parent formulario = loader.load();
        AddEmployeeForm controller = loader.getController();
        controller.setOnSaveSuccess(this::loadEmployees);
        Stage curStage = ((Stage) newEmployee.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Funcionário");
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