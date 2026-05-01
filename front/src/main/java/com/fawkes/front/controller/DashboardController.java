package com.fawkes.front.controller;

import com.fawkes.front.components.OrdersCard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController {
    @FXML private VBox container;
    @FXML
    public void initialize(){
        OrdersCard card=new OrdersCard();
        System.out.println("DashboardController.initialize() chamado");
        container.getChildren().add(card);
        container.getChildren().add(new Label("test"));
    }
}
