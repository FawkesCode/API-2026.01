package com.fawkes.front.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.models.HistoryLog;
import com.fawkes.front.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class HistoryLogCard extends AnchorPane {

    @FXML private Label status;
    @FXML private Label typeLabel;

    public HistoryLogCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/HistoryLogCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Renderiza um evento de pedido vindo de /api/purchase-orders/events. */
    public void setOrderEvent(JsonNode node) {
        String toStatus = node.path("toStatus").asText("");
        String label    = node.path("orderLabel").asText("Pedido");
        String by       = node.path("performedBy").asText("-");
        String reason   = node.path("reason").asText("");
        String when     = HistoryLog.formatDate(node.path("occurredAt"));

        if (status != null) status.setText(when);

        if (typeLabel != null) {
            typeLabel.setText("Pedido — "
                    + StringUtils.requestStatusTranslation(toStatus));
            String style = orderStatusStyle(toStatus);
            if (!style.isEmpty()) typeLabel.getStyleClass().addAll("requests__status", style);
        }

        Label ownerLabel = (Label) this.lookup(".log__owner");
        if (ownerLabel != null) ownerLabel.setText(label);

        Label descLabel = (Label) this.lookup(".log__description");
        if (descLabel != null) {
            String txt = "Por: " + by;
            if (!reason.isBlank() && !"null".equals(reason)) {
                txt += "  —  " + reason;
            }
            descLabel.setText(txt);
        }
    }

    private String orderStatusStyle(String toStatus) {
        return switch (toStatus) {
            case "pending"   -> "requests__status--pending";
            case "quoted"    -> "requests__status--quoted";
            case "confirmed" -> "requests__status--approved";
            case "shipped"   -> "requests__status--shipped";
            case "received"  -> "requests__status--received";
            case "cancelled" -> "requests__status--cancelled";
            case "problem"   -> "requests__status--problem";
            case "overdue"   -> "requests__status--overdue";
            case "returned"  -> "requests__status--returned";
            default          -> "";
        };
    }

    public void setData(HistoryLog log) {
        boolean isInput = log.getType() == HistoryLog.MovementType.ENTRADA;

        if (status != null) {
            status.setText(log.getDate());
        }

        if (typeLabel != null) {
            typeLabel.setText(isInput ? "Entrada - Estoque" : "Saída - Estoque");
            typeLabel.getStyleClass().addAll("requests__status", switch (log.getType()) {
                case HistoryLog.MovementType.ENTRADA -> "requests__status--approved";
                case HistoryLog.MovementType.SAIDA -> "requests__status--cancelled";
                default -> "";

            });

            typeLabel.setStyle(isInput
                    ? "-fx-text-fill: #4FE481;"
                    : "-fx-text-fill: #E14B50;");
        }

        Label ownerLabel = (Label) this.lookup(".log__owner");
        if (ownerLabel != null) {
            ownerLabel.setText(log.getProductName());
        }

        Label descLabel = (Label) this.lookup(".log__description");
        if (descLabel != null) {
            descLabel.setText(
                    (isInput ? "+ " : "- ") + log.getQuantity()
                            + " unidade(s)  —  Por: " + log.getResponsible()
            );
        }
    }
}