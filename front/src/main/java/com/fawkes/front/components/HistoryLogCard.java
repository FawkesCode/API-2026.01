package com.fawkes.front.components;

import com.fawkes.front.models.HistoryLog;
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

    public void setData(HistoryLog log) {
        boolean isInput = log.getType() == HistoryLog.MovementType.ENTRADA;

        if (status != null) {
            status.setText(log.getDate());
        }

        if (typeLabel != null) {
            typeLabel.setText(isInput ? "Entrada - Estoque" : "Saída - Estoque");
            typeLabel.getStyleClass().add(switch (log.getType()) {
                case HistoryLog.MovementType.ENTRADA -> "log__status--1";
                case HistoryLog.MovementType.SAIDA -> "log__status--2";
                case HistoryLog.MovementType.REVISAO -> "log__status--3";
                case HistoryLog.MovementType.APROVACAO -> "log__status--4";
                case HistoryLog.MovementType.NEGACAO -> "log__status--5";
                case HistoryLog.MovementType.COMPRA -> "log__status--6";
                case HistoryLog.MovementType.EM_TRANSITO -> "log__status--7";
                case HistoryLog.MovementType.EM_ATRASO -> "log__status--8";
                case HistoryLog.MovementType.RECEBIDO -> "log__status--9";
                case HistoryLog.MovementType.PROBLEMA -> "log__status--10";
                case HistoryLog.MovementType.NAO_RECEBIDO -> "log__status--11";
                case HistoryLog.MovementType.DEVOLVIDO -> "log__status--12";
                default -> "log__status";

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