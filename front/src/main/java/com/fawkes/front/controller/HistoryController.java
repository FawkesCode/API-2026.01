package com.fawkes.front.controller;

import com.fawkes.front.components.HistoryLogCard;
import com.fawkes.front.components.SupplierCard;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class HistoryController {
    @FXML private VBox historyContainer;

    @FXML
    public void initialize() {
        HistoryLogCard card = new HistoryLogCard();
        historyContainer.getChildren().add(card);
    }

}
