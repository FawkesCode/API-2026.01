package com.fawkes.front.controller;

import com.fawkes.front.utils.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RequestProductsPageController {
    @FXML private VBox productsContainer;

    NavigationManager nm = NavigationManager.getInstance();

    public void initialize() {
        System.out.println("Página inicializada");
    }

    public void handleAddToCart() {

    }

    public void handleSearch() {

    }

    public void handleBackButton() {
        StackPane container = (StackPane) productsContainer.getScene().getRoot().lookup("#container");
        nm.navigateToPage(container, "view/orders-page.fxml", "Pedidos de Compra", "Onde você e outros funcionários podem vizualizar os pedidos em aberto ou finalizados.");
    }



}
