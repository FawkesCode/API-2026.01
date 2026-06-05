package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.HistoryLogCard;
import com.fawkes.front.models.HistoryLog;
import com.fawkes.front.service.ApiClient;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HistoryPageController {
    @FXML private HBox filtersContainer;
    @FXML private VBox historyContainer;
    @FXML private JFXButton tabStock;
    @FXML private JFXButton tabOrders;
    private int loadGeneration = 0;

    @FXML
    public void initialize() {
        historyContainer.setMinWidth(0);
        historyContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        historyContainer.setMaxWidth(Double.MAX_VALUE);
        showStock();
    }

    @FXML
    private void showStock() {
        resetFilterButtonsActiveState();
        tabStock.getStyleClass().add("orders__filter--active");
        load("/api/stock/movements/activity", false);
    }

    @FXML
    private void showOrders() {
        resetFilterButtonsActiveState();
        tabOrders.getStyleClass().add("orders__filter--active");
        load("/api/purchase-orders/events", true);
    }

    private void load(String path, boolean isOrders) {
        final int gen = ++loadGeneration;
        historyContainer.getChildren().clear();
        historyContainer.getChildren().add(new Label("Carregando atividades..."));

        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception { return ApiClient.get(path); }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            if (gen != loadGeneration) return;
            historyContainer.getChildren().clear();
            JsonNode data = task.getValue();

            System.out.println("DADOS ATIVIDADE RECENTE:" + data.toPrettyString());

            if (data == null || !data.isArray() || data.isEmpty()) {
                setMessage(isOrders ? "Nenhum evento de pedido registrado ainda."
                                    : "Nenhuma atividade de estoque registrada ainda.");
                return;
            }
            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            flow.setAlignment(Pos.CENTER);

            for (JsonNode node : data) {
                HistoryLogCard card = new HistoryLogCard();
                if (isOrders) {
                    card.setOrderEvent(node);
                } else {
                    String type = node.path("type").asText("ENTRADA");
                    HistoryLog.MovementType movType = "SAIDA".equals(type)
                            ? HistoryLog.MovementType.SAIDA : HistoryLog.MovementType.ENTRADA;
                    card.setData(HistoryLog.fromJson(node, movType));
                }
                card.prefWidthProperty().bind(historyContainer.widthProperty());
                flow.getChildren().add(card);
            }
            historyContainer.getChildren().add(flow);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            if (gen != loadGeneration) return;
            setMessage("Erro ao carregar histórico: " + java.util.Objects.toString(
                    task.getException().getMessage(), task.getException().getClass().getSimpleName()));
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setMessage(String message) {
        historyContainer.getChildren().clear();
        Label l = new Label(message);
        l.setWrapText(true);
        historyContainer.getChildren().add(l);
    }

    private void resetFilterButtonsActiveState() {
        for (javafx.scene.Node node : filtersContainer.getChildren()) {
            if (node instanceof JFXButton) {
                node.getStyleClass().remove("orders__filter--active");
            }
        }
    }
}
