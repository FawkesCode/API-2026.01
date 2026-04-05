package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.HistoryLogCard;
import com.fawkes.front.models.HistoryLog;
import com.fawkes.front.service.ApiClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
public class HistoryPageController {

    @FXML private VBox historyContainer;

    @FXML
    public void initialize() {
        loadHistory();
    }

    public void loadHistory() {
        historyContainer.getChildren().clear();

        // Feedback imediato enquanto carrega
        Label loading = new Label("Carregando atividades...");
        loading.setStyle("-fx-text-fill: #9a9ea5; -fx-font-size: 13px; -fx-padding: 8 0;");
        historyContainer.getChildren().add(loading);

        // Chamada HTTP em thread separada — não trava a UI
        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/stock/movements/activity");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            historyContainer.getChildren().clear();
            JsonNode data = task.getValue();

            if (!data.isArray() || data.isEmpty()) {
                addMessage("Nenhuma atividade registrada ainda.");
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
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            historyContainer.getChildren().clear();
            addMessage("Erro ao carregar histórico: " + task.getException().getMessage());
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void addMessage(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-text-fill: #9a9ea5; -fx-font-size: 13px; -fx-padding: 8 0;");
        label.setWrapText(true);
        historyContainer.getChildren().add(label);
    }
}