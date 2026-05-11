package com.fawkes.front.components;

import com.fawkes.front.models.Employee;
import com.fawkes.front.models.Order;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.function.Consumer;

public class OrdersCard extends AnchorPane {
    @FXML private Label status;
    @FXML private Label solicitorName;
    @FXML private Label department;
    @FXML private Label paymentMethod;
    @FXML private Label quantityValue;
    @FXML private Label priceValue;
    @FXML private Button purchaseDetails;

    private Order order;
    private Consumer<Order> onViewAction;

    public void setOnViewAction(Consumer<Order> action) {
        this.onViewAction = action;
    }

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

    public void setData(Order order) {
        status.getStyleClass().removeAll("order__status--aproved", "order__status--declined");
        this.status.setText(StringUtils.requestStatusTranslation(order.getStatus()));
        if (order.getStatus().equalsIgnoreCase("confirmed")) {
//            status.getStyleClass().add("order__status--aproved");
            status.setStyle("-fx-background-color: #07b0f3;");
        } else if (order.getStatus().equalsIgnoreCase("cancelled")) {
            status.getStyleClass().add("order__status--declined");
            status.setStyle("-fx-background-color: #282e38;");
        }

        this.solicitorName.setText("Solicitado por "+ order.getRequesterName());
        this.department.setText(order.getSector().toUpperCase() + " | Solicitação de Compra");
        this.paymentMethod.setText("Método de pagamento: "+ StringUtils.paymentTranslation(order.getPaymentMethod()).toUpperCase());
        this.quantityValue.setText("Quantidade de Itens: " + order.getQuantity());
        this.priceValue.setText("Valor Total: " + order.getTotalValue());

        this.order = order;
    }

    @FXML
    public void openViewModal(){
        if (onViewAction != null) {
            onViewAction.accept(this.order);
        }
    }
}