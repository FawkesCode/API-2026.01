package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.HistoryLogCard;
import com.fawkes.front.models.HistoryLog;
import com.fawkes.front.service.ApiClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
public class HistoryPageController {

    @FXML private VBox historyContainer;

    @FXML
    public void initialize() {
        historyContainer.setMinWidth(0);
        historyContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        historyContainer.setMaxWidth(Double.MAX_VALUE);

        loadHistory();
    }

    public void loadHistory() {
        historyContainer.getChildren().clear();

        Label loading = new Label("Carregando atividades...");
        historyContainer.getChildren().add(loading);

        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/stock/movements/activity");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            historyContainer.getChildren().clear();
            JsonNode data = task.getValue();
            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            flow.setAlignment(Pos.CENTER);

            System.out.println(task.getValue().toPrettyString());

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhuma atividade registrada ainda.");
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
                card.prefWidthProperty().bind(historyContainer.widthProperty());
                flow.getChildren().add(card);
            }

            historyContainer.getChildren().add(flow);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            historyContainer.getChildren().clear();
            setErrorMessage("Erro ao carregar histórico: " + task.getException().getMessage());
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setErrorMessage(String message) {
        historyContainer.getChildren().clear();
        Label statusLabel = new Label(message);
        statusLabel.setWrapText(true);
        historyContainer.getChildren().add(statusLabel);
    }
}