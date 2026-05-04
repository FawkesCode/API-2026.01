package com.fawkes.front.controller;

import com.fawkes.front.models.*;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PendingRequestForm {
    @FXML private JFXButton btnApprove;
    @FXML private JFXButton btnDecline;
    @FXML private Label costCenter;
    @FXML private Label department;
    @FXML private Label description;
    @FXML private Label paymentMethod;
    @FXML private VBox productsContainer;
    @FXML private Label requisitor;
    @FXML private VBox suppliersContainer;
    @FXML private Label totalPrice;
    @FXML private Label totalQuantity;

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private Stage curStage;
    private Order order;

    public void setData(Order order, Stage curStage) {
        this.curStage = curStage;
        this.order = order;
        department.setText(order.getSector());
        description.setText(order.getDescription());
        paymentMethod.setText(StringUtils.paymentTranslation(order.getPaymentMethod()));
        requisitor.setText(order.getRequesterName());
        totalPrice.setText("Total: " + CURRENCY.format(order.getTotalValue()));
        totalQuantity.setText("Qtd. de Itens: " + order.getQuantity());


        List<RequestItem> productsInfo = order.getItemsList();
        List<RequestSupplier> suppliersInfo = order.getSuppliersList();
        List<FormProducts> products = new ArrayList<>();


        for (RequestItem pro : productsInfo) {
            String name = pro.getProduct().getName();
            double price = pro.getUnitPrice();
            int quantity = pro.getQuantity();

            FormProducts product = new FormProducts(name, price, quantity);

            products.add(product);
        }

        for (FormProducts pro : products) {
            // 1. Criar os Labels
            Label qtd = new Label("(x " + pro.getQuantity() + ")");
            qtd.getStyleClass().add("input__label--info");

            Label name = new Label(pro.getName());
            name.getStyleClass().add("input__label--info");

            Label price = new Label(pro.getUnityPrice());
            price.getStyleClass().add("input__label--info");


            HBox productsLineContainer = new HBox(5);
            productsLineContainer.setAlignment(Pos.CENTER);



            StackPane spacer = new StackPane();
            spacer.setStyle("-fx-border-style: dotted; -fx-border-color: #818EA1; -fx-border-width: 0 0 3 0;");
            spacer.setMinHeight(5);
            spacer.setMaxHeight(5);
            HBox.setHgrow(spacer, Priority.ALWAYS);


            productsLineContainer.getChildren().addAll(qtd, name, spacer, price);


            productsContainer.getChildren().add(productsLineContainer);
        }

        for (RequestSupplier sup: suppliersInfo) {
            Label supplier = new Label(sup.getSupplierName());
            supplier.getStyleClass().add("input__label--info");

            suppliersContainer.getChildren().add(supplier);
        }
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnApprove.getScene().getWindow()).close();
    }

    @FXML
    public void handleAproved() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/director-request-form.fxml"));
            AproveRequestForm controller = new AproveRequestForm();
            loader.setController(controller);
            Parent formulario = loader.load();
            controller.setData(order);

            Stage stageAtual = (Stage) btnApprove.getScene().getWindow();

            Platform.runLater(() -> {
                stageAtual.close();
                ModalManager.openModal(curStage, formulario, "Aprovando Pedido " + order.getId(), 700.0, 350.0, "ModalFrameM_heightSM.fxml", false);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeclined() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/director-request-form.fxml"));
            DeclineRequestForm controller = new DeclineRequestForm();
            loader.setController(controller);
            Parent formulario = loader.load();
            controller.setData(order);

            Stage stageAtual = (Stage) btnApprove.getScene().getWindow();

            Platform.runLater(() -> {
                stageAtual.close();
                ModalManager.openModal(curStage, formulario, "Recusando Pedido " + order.getId(), 700.0, 350.0, "ModalFrameM_heightSM.fxml", false);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
