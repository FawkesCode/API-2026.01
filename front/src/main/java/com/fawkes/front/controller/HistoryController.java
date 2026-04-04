package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.HistoryLogCard;
import com.fawkes.front.models.HistoryLog;
import com.fawkes.front.service.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/*
 Controller da tela de Atividade Recente.

 Depende de dois endpoints GET que ainda precisam ser criados no back:
    GET /api/stock/movements/inputs   → lista ProductInputs
    GET /api/stock/movements/outputs  → lista ProductOutputs

 Enquanto esses endpoints não existirem, a tela exibe uma mensagem orientativa.
 Assim que o back os implementar, basta reiniciar o front — nenhuma alteração necessária aqui.
 */
public class HistoryController {

    @FXML private VBox  historyContainer;

    @FXML
    public void initialize() {
        loadHistory();
    }

    public void loadHistory() {
        historyContainer.getChildren().clear();

        int count = 0;

        // Entradas
        try {
            JsonNode inputs = ApiClient.get("/api/stock/movements/inputs");
            for (JsonNode node : inputs) {
                HistoryLog log = HistoryLog.fromJson(node, HistoryLog.MovementType.ENTRADA);
                HistoryLogCard card = new HistoryLogCard();
                card.setData(log);
                historyContainer.getChildren().add(card);
                count++;
            }
        } catch (Exception e) {
            addWarning("Entradas indisponíveis: endpoint GET /api/stock/movements/inputs ainda não implementado no back.");
        }

        // Saídas
        try {
            JsonNode outputs = ApiClient.get("/api/stock/movements/outputs");
            for (JsonNode node : outputs) {
                HistoryLog log = HistoryLog.fromJson(node, HistoryLog.MovementType.SAIDA);
                HistoryLogCard card = new HistoryLogCard();
                card.setData(log);
                historyContainer.getChildren().add(card);
                count++;
            }
        } catch (Exception e) {
            addWarning("Saídas indisponíveis: endpoint GET /api/stock/movements/outputs ainda não implementado no back.");
        }

        if (count == 0 && historyContainer.getChildren().isEmpty()) {
            Label empty = new Label("Nenhuma atividade registrada ainda.");
            empty.setStyle("-fx-text-fill: #9a9ea5; -fx-font-size: 14px;");
            historyContainer.getChildren().add(empty);
        }
    }

    private void addWarning(String msg) {
        Label warn = new Label(msg);
        warn.setStyle("-fx-text-fill: #FF4A50; -fx-font-size: 12px; -fx-padding: 4 0;");
        warn.setWrapText(true);
        historyContainer.getChildren().add(warn);
    }
}