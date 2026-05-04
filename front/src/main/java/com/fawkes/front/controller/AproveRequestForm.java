package com.fawkes.front.controller;

import com.fawkes.front.models.FormProducts;
import com.fawkes.front.models.Order;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.models.RequestSupplier;
import com.fawkes.front.service.UserInfoManager;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AproveRequestForm {
    @FXML private Label detailsLabel;
    @FXML private JFXButton btnCancel;
    @FXML private JFXButton btnCommand;
    @FXML private TextArea descriptionField;
    UserInfoManager loggedUser = UserInfoManager.getInstance();

    public void initialize() {
        btnCommand.setText("Aprovar");
    }


    public void setData(Order order) {

        StringBuilder productsList = new StringBuilder();
        for (RequestItem pro : order.getItemsList()) {
            productsList.append(pro.getProduct().getName());
            productsList.append(",");
        }

        if (productsList.length() > 2) {
            productsList.setLength(productsList.length() - 2);
        }


        String text = "Pedido de " + order.getQuantity() + " itens, sendo eles: " + productsList + "; Total de " + order.getTotalValue() + ". Compra pedida pelo " + order.getRequesterName() + " para o setor " + order.getSector().toUpperCase() + ". Solicitação sendo aprovada por " + loggedUser.getUserName() + ".";

        detailsLabel.setText(text);
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnCommand.getScene().getWindow()).close();
    }


}
