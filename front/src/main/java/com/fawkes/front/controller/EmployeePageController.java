package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawkes.front.components.EmployeeCard;
import com.fawkes.front.models.Employee;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.RBACUtil;
import com.fawkes.front.utils.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
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
    @FXML private Button    newEmployee;
    @FXML private Label activeUsersLabel;
    @FXML private Label inactiveUsersLabel;

    // Dialog de cadastro
    //@FXML private VBox          addDialog;
    @FXML private TextField     fieldUsername;
    @FXML private TextField     fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private ComboBox<String> fieldGroup;
    @FXML private TextField     fieldDept;
    @FXML private Label         addErrorLabel;

    @FXML private VBox groupsContainer;

    private final ObservableList<JsonNode> allRows = FXCollections.observableArrayList();
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Employee> allEmployees = new ArrayList<>();
    private Label statusLabel = new Label();

    @FXML
    public void initialize() {
        // Apply RBAC restrictions based on user role
        applyRBACRestrictions();

        loadEmployees();
    }

    private void applyRBACRestrictions() {
        // Only DIRECTOR and MANAGER can create new employees
        if (!RBACUtil.canManageEmployees()) {
            newEmployee.setVisible(false);
            newEmployee.setManaged(false);
        }
    }

    private void setErrorMessage(String message) {
        groupsContainer.getChildren().clear();
        statusLabel.setText(message);
        groupsContainer.getChildren().add(statusLabel);
    }

    @FXML
    public void loadEmployees() {
        groupsContainer.getChildren().clear();
        List<Employee> activeUsers = new ArrayList<>();
        List<Employee> inactiveUsers = new ArrayList<>();

        Label loading = new Label("Carregando funcionários...");
        groupsContainer.getChildren().add(loading);

        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/users");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            groupsContainer.getChildren().clear();
            JsonNode data = task.getValue();

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhum funcionário cadastrado.");
                return;
            }

            allEmployees.clear();
            for (JsonNode node: data) {
                allEmployees.add(Employee.fromJson(node));
            }

            for (JsonNode node: data) {
                Employee emp = Employee.fromJson(node);
                String status = emp.getStatus().toLowerCase();

                if (status.equals("ativo")) {
                    activeUsers.add(emp);
                } else {
                    inactiveUsers.add(emp);
                }
            }

            int qtdActiveUsers = activeUsers.toArray().length;
            int qtdInactiveUsers = inactiveUsers.toArray().length;

            renderEmployeesGroup(allEmployees);

            activeUsersLabel.setText(qtdActiveUsers + " Ativos");
            inactiveUsersLabel.setText(qtdInactiveUsers + " Inativos");
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            groupsContainer.getChildren().clear();
            setErrorMessage("Erro ao carregar funcionários: " + task.getException().getMessage());
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void renderEmployeesGroup(List<Employee> employees) {
        groupsContainer.getChildren().clear();

        java.util.Map<String, java.util.List<Employee>> byGroup = new java.util.LinkedHashMap<>();

        for (Employee emp : employees) {
            String group = emp.getPosition();
            byGroup.computeIfAbsent(emp.getPosition(), k -> new ArrayList<>()).add(emp);
        }

        // Renderiza um bloco por grupo
        for (java.util.Map.Entry<String, java.util.List<Employee>> entry : byGroup.entrySet()) {
            VBox groupInfo = new VBox();
            Label groupLabel = new Label("Usuários com acesso de " + StringUtils.roleTranslation(entry.getKey().toUpperCase()).toLowerCase());
            groupLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37404C; "
                    + "-fx-padding: 16px 0px 8px 0px;");
            Label groupDesc = new Label(StringUtils.roleDescTranslation(entry.getKey().toUpperCase()));
            groupDesc.setStyle("-fx-font-family: 'Poppins Regular'; -fx-text-fill: #556376;");

            groupInfo.getChildren().addAll(groupLabel, groupDesc);

            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            if (entry.getValue().size() < 3) {
                flow.setAlignment(Pos.CENTER_LEFT);
            } else {
                flow.setAlignment(Pos.CENTER);
            }
            flow.setStyle("-fx-background-color: #ffffff; -fx-padding: 16px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: #DADEE3; -fx-border-style: solid;");


            for (Employee emp : entry.getValue()) {
                EmployeeCard card = new EmployeeCard();
                card.setData(emp);

                card.setOnEditAction(this::openEditEmployee);

                flow.getChildren().add(card);
            }

            groupsContainer.getChildren().addAll(groupInfo, flow);
        }
    }

    private void openEditEmployee(Employee emp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/edit-employee-form.fxml"));
            Parent formulario = loader.load();
            EditEmployeeForm controller = loader.getController();
            controller.setOnSaveSuccess(this::loadEmployees);
            controller.setEmployeeData(emp);
            Stage curStage = ((Stage) groupsContainer.getScene().getWindow());
            ModalManager.openModal(curStage, formulario, "Editar Funcionário: " + emp.getName(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            renderEmployeesGroup(allEmployees);
            return;
        }

        List<Employee> filtered = allEmployees.stream().filter(emp -> emp.getName().toLowerCase().contains(query) || emp.getEmail().toLowerCase().contains(query) || StringUtils.roleTranslation(emp.getPosition()).toLowerCase().contains(query)).toList();

        renderEmployeesGroup(filtered);

        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum funcionário encontrado.");
        }
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