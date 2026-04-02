package com.fawkes.front.controller;

import com.fawkes.front.components.SupplierCard;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SuppliersController {
    @FXML private VBox suppliersContainer;

    @FXML
    public void initialize() {
        SupplierCard card = new SupplierCard();
        suppliersContainer.getChildren().add(card);
    }


}
