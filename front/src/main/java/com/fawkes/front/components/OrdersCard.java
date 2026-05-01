package com.fawkes.front.components;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class OrdersCard extends AnchorPane {
    @FXML private Label orderChecker;
    @FXML private Label solicitorName;
    @FXML private Label solicitationTitle;
    @FXML private Label paymentMethod;
    @FXML private Label quantityValue;
    @FXML private Label priceValue;
    @FXML private Button purchaseDetails;

public OrdersCard() {
    FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/com/fawkes/front/view/components/OrdersCard.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
        fxmlLoader.load();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
}