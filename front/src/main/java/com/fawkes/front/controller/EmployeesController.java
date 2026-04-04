package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.EmployeeCard;
import com.fawkes.front.models.Employee;
import com.fawkes.front.service.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/*
 Controller da tela de funcionários.
 Separa os funcionários por grupo (permissão) e cria um EmployeeCard para cada um.

 Estrutura esperada no FXML (employees-page.fxml):
 - VBox com fx:id="groupsContainer" onde cada grupo é adicionado dinamicamente
 - Label com fx:id="statusLabel" para feedback de erro/carregamento

 Cada grupo é renderizado como:
 Label (nome do grupo) + FlowPane (cards dos funcionários desse grupo)
 */

public class EmployeesController {

    @FXML private VBox groupsContainer;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        loadEmployees();
    }

    @FXML
    public void loadEmployees() {
        groupsContainer.getChildren().clear();

        try {
            JsonNode data = ApiClient.get("/api/users");

            if (!data.isArray() || data.isEmpty()) {
                statusLabel.setText("Nenhum funcionário encontrado.");
                return;
            }

            // Agrupa os funcionários pelo nome do grupo (cargo/permissão)
            java.util.Map<String, java.util.List<Employee>> byGroup = new java.util.LinkedHashMap<>();

            for (JsonNode node : data) {
                Employee emp = Employee.fromJson(node);
                String group = emp.getPosition(); // group.groupName vira position no model
                byGroup.computeIfAbsent(group, k -> new java.util.ArrayList<>()).add(emp);
            }

            // Renderiza um bloco por grupo
            for (java.util.Map.Entry<String, java.util.List<Employee>> entry : byGroup.entrySet()) {
                Label groupLabel = new Label(entry.getKey().toUpperCase());
                groupLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; "
                        + "-fx-padding: 16px 0px 8px 0px;");

                FlowPane flow = new FlowPane();
                flow.setHgap(16);
                flow.setVgap(16);

                for (Employee emp : entry.getValue()) {
                    EmployeeCard card = new EmployeeCard();
                    card.setData(emp);
                    flow.getChildren().add(card);
                }

                groupsContainer.getChildren().addAll(groupLabel, flow);
            }

            statusLabel.setText("");

        } catch (Exception e) {
            statusLabel.setText("Erro ao carregar funcionários: " + e.getMessage());
        }
    }
}