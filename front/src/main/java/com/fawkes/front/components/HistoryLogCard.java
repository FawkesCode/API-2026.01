package com.fawkes.front.components;

import com.fawkes.front.models.HistoryLog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class HistoryLogCard extends AnchorPane {

    // O FXML atual só tem fx:id="status" (exibe a data no lado direito).
    // Os outros labels (log__type, log__owner, log__description) não têm fx:id,
    // então são acessados via lookup por styleClass após o carregamento.
    @FXML private Label status;

    public HistoryLogCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/HistoryLogCard.fxml"));
        // String css = Objects.requireNonNull(
                        // getClass().getResource("/com/fawkes/front/styles/components/history.scss"))
                // .toExternalForm();
        //this.getStylesheets().add(css);
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

        // fx:id disponível: data no lado direito
        if (status != null) {
            status.setText(log.getDate());
        }

        // Labels sem fx:id — acessados por styleClass
        Label typeLabel = (Label) this.lookup(".log__type");
        if (typeLabel != null) {
            typeLabel.setText(isInput ? "Entrada - Estoque" : "Saída - Estoque");
            typeLabel.setStyle(isInput
                    ? "-fx-text-fill: #2e7d32;"
                    : "-fx-text-fill: #FF4A50;");
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