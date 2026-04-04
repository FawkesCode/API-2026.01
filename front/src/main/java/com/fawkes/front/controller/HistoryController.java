package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.HistoryLogCard;
import com.fawkes.front.models.HistoryLog;
import com.fawkes.front.service.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HistoryController {

    @FXML private VBox historyContainer;

    @FXML
    public void initialize() {
        loadHistory();
    }

    public void loadHistory() {
        historyContainer.getChildren().clear();

        try {
            // Endpoint unificado — retorna entradas e saídas já ordenadas
            // por data decrescente (mais recente primeiro)
            JsonNode data = ApiClient.get("/api/stock/movements/activity");

            if (!data.isArray() || data.isEmpty()) {
                addEmpty("Nenhuma atividade registrada ainda.");
                return;
            }

            for (JsonNode node : data) {
                String type = node.path("type").asText("ENTRADA");
                HistoryLog.MovementType movType = "SAIDA".equals(type)
                        ? HistoryLog.MovementType.SAIDA
                        : HistoryLog.MovementType.ENTRADA;

                HistoryLog log = HistoryLog.fromJson(node, movType);
                HistoryLogCard card = new HistoryLogCard();
                card.setData(log);
                historyContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            addEmpty("Erro ao carregar histórico: " + e.getMessage());
        }
    }

    private void addEmpty(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-text-fill: #9a9ea5; -fx-font-size: 13px; -fx-padding: 8 0;");
        label.setWrapText(true);
        historyContainer.getChildren().add(label);
    }
}