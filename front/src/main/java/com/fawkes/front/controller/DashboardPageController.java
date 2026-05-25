package com.fawkes.front.controller;

import com.fawkes.front.models.LastOrders;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DashboardPageController {
    @FXML private VBox dashboardContainer;

    @FXML
    public void initialize() {
        loadDashboard();
    }

    private void loadDashboard() {
        renderDashboardGroups();
    }

    private void renderAlertsCard() {
        // Tranportar o Código de Alertas para ca
    }

    private TableView<LastOrders> renderOrdersTable() {
        TableView<LastOrders> lastOrdersTable= new TableView<>();
        lastOrdersTable.setPrefHeight(400);

        TableColumn<LastOrders, String> columnId = new TableColumn<>("ID");
        TableColumn<LastOrders, String> columnSuppliers = new TableColumn<>("Fornecedor");
        TableColumn<LastOrders, String> columnSolicitor = new TableColumn<>("Solicitante");
        TableColumn<LastOrders, String> columnValue = new TableColumn<>("Valor");
        TableColumn<LastOrders, String> columnStatus = new TableColumn<>("Status");
        TableColumn<LastOrders, String> columnDeliveryDate = new TableColumn<>("Previsão Entrega");

        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnSuppliers.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        columnSolicitor.setCellValueFactory(new PropertyValueFactory<>("solicitor"));
        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        columnDeliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));

        lastOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lastOrdersTable.getColumns().addAll(columnId, columnSolicitor, columnValue, columnStatus, columnDeliveryDate);

        return lastOrdersTable;
    }

    private void renderDashboardGroups() {
        dashboardContainer.getChildren().clear();
        //PRIMEIRA LINHA DE CARDS
        HBox firstRow = new HBox();

        // SEGUNDA LINHA DE CARDS
        HBox secondRow = new HBox();
        Label monthShops = new Label("Compras por mês");
        monthShops.getStyleClass().add("dashboard__title");
        Label topSuppliers = new Label("Top Fornecedores");
        topSuppliers.getStyleClass().add("dashboard__title");
        Label topProducts = new Label("Produtos Críticos");
        topProducts.getStyleClass().add("dashboard__title");
        Label ordersStatus = new Label("Compras por mês");
        ordersStatus.getStyleClass().add("dashboard__title");
        VBox monthShopsContainer = new VBox(monthShops);
        VBox topSuppliersContainer = new VBox(topSuppliers, topProducts);
        VBox ordersStatusContainer = new VBox(ordersStatus);

        monthShopsContainer.getStyleClass().add("dashboard__cards-container");
        monthShopsContainer.setMinHeight(350);
        HBox.setHgrow(monthShopsContainer, Priority.ALWAYS);
        topSuppliersContainer.getStyleClass().add("dashboard__cards-container");
        topSuppliersContainer.setMinHeight(350);
        HBox.setHgrow(topSuppliersContainer, Priority.ALWAYS);
        ordersStatusContainer.getStyleClass().add("dashboard__cards-container");
        ordersStatusContainer.setMinHeight(350);
        HBox.setHgrow(ordersStatusContainer, Priority.ALWAYS);

        secondRow.setSpacing(20);
        secondRow.getChildren().addAll(monthShopsContainer, topSuppliersContainer, ordersStatusContainer);

        // CARD DE TABELA
        VBox tableContainer = new VBox();
        HBox.setHgrow(tableContainer, Priority.ALWAYS);
        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        tableContainer.setSpacing(10);

        Label title = new Label("Pedidos já aprovados");
        title.getStyleClass().add("dashboard__title");
        JFXButton btnSeeAllOrders = new JFXButton("Ver Todos >>");
        btnSeeAllOrders.getStyleClass().add("dashboard__button");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        titleContainer.getChildren().addAll(title, spacer, btnSeeAllOrders);

        tableContainer.setMinWidth(600);
        tableContainer.getStyleClass().add("dashboard__cards-container");
        tableContainer.getChildren().addAll(titleContainer, renderOrdersTable());


        // CARD DE ALERTAS
        VBox alertsContainer = new VBox();
        alertsContainer.setMinWidth(320);
        alertsContainer.setPrefWidth(320);
        alertsContainer.setMaxWidth(320);

        alertsContainer.setMinHeight(400);
        alertsContainer.setPrefHeight(400);
        alertsContainer.setMaxHeight(400);
        alertsContainer.setAlignment(Pos.TOP_LEFT);
        alertsContainer.getStyleClass().add("dashboard__alerts-container");

        VBox alertsContent = new VBox();
        alertsContent.setSpacing(10);
        alertsContent.setAlignment(Pos.TOP_LEFT);

        ScrollPane alertsScroll = new ScrollPane();
        alertsScroll.setContent(alertsContent);
        alertsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        alertsScroll.getStyleClass().add("custom-scroll");

        alertsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        alertsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        alertsScroll.setFitToWidth(true);
        VBox.setVgrow(alertsScroll, Priority.ALWAYS);


        // CONTEUDO DE ALERTAS
        Label alertsTitle = new Label("Atenção");
        alertsTitle.getStyleClass().add("dashboard__alerts--title");

        VBox alertsOrdersPrices = new VBox();
        Label label1 = new Label("Total gasto em pedidos");
        label1.getStyleClass().add("dashboard__alerts--subtitle");
        Label label2 = new Label("R$ 1200,00");
        label2.getStyleClass().add("dashboard__alerts--price");
        Label label3 = new Label("em 10 compras realizadas e recebidas");
        label3.getStyleClass().add("dashboard__alerts--info");
        alertsOrdersPrices.getChildren().addAll(label1, new VBox(label2, label3));

        VBox productsNearMin = new VBox();
        Label proLabel1 = new Label("Produtos que estão perto do mínimo ");
        proLabel1.getStyleClass().add("dashboard__alerts--subtitle");
        VBox productsList = new VBox();
        productsList.setSpacing(5);

        // COMPONENTE PARA PRODUTOS
        Label productsCard = new Label("Caneta -> 10 restantes");
        productsCard.getStyleClass().add("dashboard__alerts--products");
        productsCard.setMaxWidth(Double.MAX_VALUE);
        productsCard.setAlignment(Pos.CENTER_LEFT);

        productsList.getChildren().addAll(productsCard);
        productsNearMin.setSpacing(10);
        productsNearMin.getChildren().addAll(proLabel1, productsList);

        VBox ordersProbs = new VBox();
        Label ordLabel1 = new Label("Complicações com Pedidos");

        ordLabel1.getStyleClass().add("dashboard__alerts--subtitle");
        VBox ordersList = new VBox();
        ordersList.setSpacing(5);

        //COMPONENTE DE PEDIDOS COM PROBLEMAS
        VBox order = new VBox();
        order.getStyleClass().add("dashboard__alerts--orders-container");
        Label ordLabel2 = new Label("Pedido PED-101");
        ordLabel2.getStyleClass().add("dashboard__alerts--orders-title");
        Label ordLabel3 = new Label("NEGADO - Diretor X");
        ordLabel3.getStyleClass().add("dashboard__alerts--orders-subtitle");


        order.getChildren().addAll(ordLabel2, ordLabel3);
        ordersList.getChildren().addAll(order);
        ordersProbs.setSpacing(10);
        ordersProbs.getChildren().addAll(ordLabel1, ordersList);





        alertsContent.getChildren().addAll(alertsTitle, alertsOrdersPrices, productsNearMin, ordersProbs);
        alertsContainer.getChildren().add(alertsScroll);

        firstRow.setSpacing(10);
        firstRow.getChildren().addAll(tableContainer, new Region(), alertsContainer);
        dashboardContainer.getChildren().addAll(firstRow, secondRow);

    }



}
