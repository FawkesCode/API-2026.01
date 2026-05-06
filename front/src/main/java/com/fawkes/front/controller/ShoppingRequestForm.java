package com.fawkes.front.controller;

import com.fawkes.front.components.ProductSupplierCard;
import com.fawkes.front.components.RequestedProductCard;
import com.fawkes.front.models.FormProducts;
import com.fawkes.front.models.RequestItem;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.utils.ModalManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ShoppingRequestForm {
    @FXML private VBox itensContainer;
    @FXML private VBox productsViewContainer;
    @FXML private Label requestTotal;
    @FXML private Label requestTotalItens;

    List<StockItem> productItens;
    List<FormProducts> productsView = new ArrayList<>();
    List<String> suppliersList = new ArrayList<>();
    private int totalQtd;
    private double totalPrice;

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public void setData(List<StockItem> items) {
        productItens = items;
        renderCart();
    }

    private void renderCart() {
        if (productItens == null) return;
        productsView.clear();
        itensContainer.getChildren().clear();

        java.util.Map<String, java.util.List<StockItem>> byGroup = new java.util.LinkedHashMap<>();

        for (StockItem pro : productItens) {
            System.out.println(pro.getProductName());
            byGroup.computeIfAbsent(pro.getSupplierName(), k -> new ArrayList<>()).add(pro);
        }

        for (java.util.Map.Entry<String, java.util.List<StockItem>> entry : byGroup.entrySet()) {
            FlowPane flow = new FlowPane();
            flow.setHgap(5);
            flow.setVgap(5);
            flow.setAlignment(Pos.TOP_LEFT);
            flow.getStyleClass().add("shopItem");

            HBox supplierInfoContainer = new HBox();
            ImageView supplierIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/supplier-icon.png"))));
            supplierIcon.setFitHeight(18);
            supplierIcon.setFitWidth(18);
            Label supplierName = new Label("Itens do " + entry.getKey().toLowerCase());
            supplierInfoContainer.setSpacing(10);
            supplierName.getStyleClass().add("shopItem__supplier");
            supplierInfoContainer.setAlignment(Pos.CENTER_LEFT);
            supplierInfoContainer.getChildren().addAll(supplierIcon, supplierName);


            VBox itemInfoContainer = new VBox();

            for (StockItem product: entry.getValue()) {
                RequestedProductCard card = new RequestedProductCard();
                card.setData(product);
                suppliersList.add(product.getSupplierName());

                FormProducts pro = card.getProduct();
                productsView.add(pro);

                card.setOnAddItem(p -> {
                    handleAdd(p);
                    renderProductsView();
                });
                card.setOnRemoveItem(p -> {
                    handleRemove(p);
                    renderProductsView();
                });
                card.setOnDeleteItem(p -> {
                    productsView.remove(p);
                    productItens.removeIf(item -> Objects.equals(Integer.parseInt(item.getProductId().toString()), p.getId()));

                    itensContainer.getChildren().clear();
                    renderCart();
                    renderProductsView();
                });


                itemInfoContainer.getChildren().add(card);

            }

            renderProductsView();
            flow.getChildren().addAll(supplierInfoContainer, itemInfoContainer);

            itensContainer.getChildren().add(flow);
        }
    }

    private void renderProductsView() {
        productsViewContainer.getChildren().clear();
        totalQtd = 0;
        totalPrice = 0.0;

        for (FormProducts p : productsView) {
            Label qtd = new Label("(x " + p.getQuantity() + ")");
            qtd.getStyleClass().add("input__label--info");

            Label name = new Label(p.getName());
            name.getStyleClass().add("input__label--info");

            Label price = new Label(String.format("R$ %.2f", p.getTotalValue()));
            price.getStyleClass().add("input__label--info");

            HBox productsLineContainer = new HBox(5);
            productsLineContainer.setAlignment(Pos.CENTER);

            StackPane spacer = new StackPane();
            spacer.setStyle("-fx-border-style: dotted; -fx-border-color: #818EA1; -fx-border-width: 0 0 3 0;");
            spacer.setMinHeight(5);
            spacer.setMaxHeight(5);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            productsLineContainer.getChildren().addAll(qtd, name, spacer, price);
            totalQtd = totalQtd + p.getQuantity();
            totalPrice = totalPrice + p.getTotalValue();

            productsViewContainer.getChildren().add(productsLineContainer);

        }
        requestTotal.setText("Total: " + CURRENCY.format(totalPrice));
        requestTotalItens.setText("Qtd. de itens: " + totalQtd);
    }

    private void handleAdd(FormProducts pro) {
        pro.setQuantity(pro.getQuantity() + 1);
    }

    private void handleRemove(FormProducts pro) {
        if (pro.getQuantity() > 1) {
            pro.setQuantity(pro.getQuantity() - 1);
        }
    }

    @FXML
    private void handleContinueRequest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-solicitor-form.fxml"));
            Parent formulario = loader.load();
            NewRequestForm controller = loader.getController();
            controller.setData(productsView, totalQtd, totalPrice, suppliersList);
            Stage curStage = ((Stage) productsViewContainer.getScene().getWindow());
            curStage.close();
            ModalManager.openModal(curStage, formulario, "Solicitar Pedido");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) productsViewContainer.getScene().getWindow()).close();
    }


}
