package com.fawkes.front.controller;

import com.fawkes.front.components.StockCard;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class StockController {
    @FXML private VBox stockContainer;

    @FXML
    public void initialize() {
        StockCard card = new StockCard();
        stockContainer.getChildren().add(card);
    }
}
