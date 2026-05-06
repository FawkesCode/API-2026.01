package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.ProductShopCard;
import com.fawkes.front.components.ProductSupplierCard;
import com.fawkes.front.models.Employee;
import com.fawkes.front.models.FormProducts;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.NavigationManager;
import com.fawkes.front.utils.StringUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestProductsPageController {
    @FXML private VBox productsContainer;
    @FXML private TextField searchField;

    NavigationManager nm = NavigationManager.getInstance();
    private List<StockItem> allProducts = new ArrayList<>();
    private List<StockItem> cartProducts = new ArrayList<>();

    private Image infoIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/info-icon.png")));
    private Image successIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/success-icon.png")));

    public void initialize() {
        loadProducts();
    }

    @FXML
    public void loadProducts() {
        productsContainer.getChildren().clear();

        Label loading = new Label("Carregando estoque...");
        productsContainer.getChildren().add(loading);

        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/products");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            productsContainer.getChildren().clear();
            JsonNode data = task.getValue();

            System.out.println("Dados da API");
            System.out.println(data.toPrettyString());

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhum produto cadastrado no estoque.");
                return;
            }

            allProducts.clear();
            for (JsonNode node: data) {
                allProducts.add(StockItem.fromJson(node));
            }

            renderProducts(allProducts);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            productsContainer.getChildren().clear();
            setErrorMessage("Erro ao carregar estoque: " + task.getException().getMessage());
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    public void renderProducts(List<StockItem> products) {
        productsContainer.getChildren().clear();

        FlowPane flow = new FlowPane();
        flow.setHgap(16);
        flow.setVgap(16);
        flow.setAlignment(Pos.TOP_LEFT);

        for (StockItem product : products) {
            ProductShopCard card = new ProductShopCard();
            card.setData(product);
            card.setOnAddToCart(this::handleAddToCart);
            flow.getChildren().add(card);
        }

        productsContainer.getChildren().add(flow);
    }


    private void setErrorMessage (String message) {
        productsContainer.getChildren().clear();
        productsContainer.getChildren().add(new Label(message));
    }

    public void handleOpenCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/shopping-request-form.fxml"));
            Parent formulario = loader.load();
            ShoppingRequestForm controller = loader.getController();
            controller.setData(cartProducts);
            Stage curStage = ((Stage) productsContainer.getScene().getWindow());
            ModalManager.openModal(curStage, formulario, "Itens a serem pedidos", 600.0, 400.0, "ModalFrameSM.fxml", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAddToCart(StockItem pro) {
        if (cartProducts.stream().noneMatch(item -> Objects.equals(item.getProductId(), pro.getProductId()))) {
            cartProducts.add(pro);
            showCartMessage(pro.getProductName() + " adicionado ao carrinho!", successIcon);
        } else {
            showCartMessage(pro.getProductName() + " já está no carrinho!", infoIcon);
        }
    }

    private void showCartMessage(String message, Image icon) {
        HBox messageContainer = new HBox();
        messageContainer.setSpacing(10);
        Label label = new Label(message);
        label.getStyleClass().add("shopItem__message");
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(18);
        imageView.setFitHeight(18);

        messageContainer.getChildren().addAll(imageView, label);

        productsContainer.getChildren().addFirst(messageContainer);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));

        pause.setOnFinished(event -> {
            productsContainer.getChildren().remove(messageContainer);
        });

        pause.play();
    }

    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            renderProducts(allProducts);
            return;
        }

        List<StockItem> filtered = allProducts.stream().filter(pro -> pro.getProductName().toLowerCase().contains(query) || pro.getProductType().toLowerCase().contains(query) || pro.getSupplierName().toLowerCase().contains(query)).toList();

        renderProducts(filtered);

        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum produto encontrado.");
        }
    }

    public void handleBackButton() {
        StackPane container = (StackPane) productsContainer.getScene().getRoot().lookup("#container");
        nm.navigateToPage(container, "view/orders-page.fxml", "Pedidos de Compra", "Onde você e outros funcionários podem vizualizar os pedidos em aberto ou finalizados.");
    }



}
