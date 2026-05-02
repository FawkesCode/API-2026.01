package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.models.Order;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersPageController {

    @FXML private VBox pageContent;

    @FXML private TableView<Order> pendingTable;
    @FXML private TableView<Order> reviewedTable;

    @FXML private TableColumn<Order, String>  colPendRequester;
    @FXML private TableColumn<Order, String>  colPendProduct;
    @FXML private TableColumn<Order, String>  colPendSector;
    @FXML private TableColumn<Order, String>  colPendPayment;
    @FXML private TableColumn<Order, Integer> colPendQuantity;
    @FXML private TableColumn<Order, Double>  colPendTotal;
    @FXML private TableColumn<Order, String>  colPendStatus;

    @FXML private TableColumn<Order, String>  colRevRequester;
    @FXML private TableColumn<Order, String>  colRevProduct;
    @FXML private TableColumn<Order, String>  colRevSector;
    @FXML private TableColumn<Order, String>  colRevPayment;
    @FXML private TableColumn<Order, Integer> colRevQuantity;
    @FXML private TableColumn<Order, Double>  colRevTotal;
    @FXML private TableColumn<Order, String>  colRevStatus;

    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    public void initialize() {
        setupColumns();
        loadOrders();
    }

    private void setupColumns() {
        colPendRequester.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
        colPendProduct  .setCellValueFactory(new PropertyValueFactory<>("product"));
        colPendSector   .setCellValueFactory(new PropertyValueFactory<>("sector"));
        colPendPayment  .setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colPendQuantity .setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPendStatus   .setCellValueFactory(new PropertyValueFactory<>("statusPtBr")); // traduzido

        colRevRequester .setCellValueFactory(new PropertyValueFactory<>("requesterName"));
        colRevProduct   .setCellValueFactory(new PropertyValueFactory<>("product"));
        colRevSector    .setCellValueFactory(new PropertyValueFactory<>("sector"));
        colRevPayment   .setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colRevQuantity  .setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colRevStatus    .setCellValueFactory(new PropertyValueFactory<>("statusPtBr")); // traduzido

        setupCurrencyColumn(colPendTotal);
        setupCurrencyColumn(colRevTotal);
    }

    private void setupCurrencyColumn(TableColumn<Order, Double> column) {
        column.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText((empty || value == null) ? null : CURRENCY_FMT.format(value));
            }
        });
    }

    @FXML
    public void handleCreateOrder(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/fawkes/front/view/forms/new-order-form.fxml")
            );
            Parent content = loader.load();

            NewOrderFormController formController = loader.getController();
            formController.setOnSuccessCallback(this::loadOrders);

            Stage currentStage = (Stage) pageContent.getScene().getWindow();
            ModalManager.openModal(currentStage, content, "Abrir Pedido", true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadOrders() {
        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/purchase-orders");
            }
        };

        task.setOnSucceeded(e -> {
            JsonNode data = task.getValue();
            List<Order> pendingList  = new ArrayList<>();
            List<Order> reviewedList = new ArrayList<>();

            if (data != null && data.isArray()) {
                for (JsonNode node : data) {
                    Order order = Order.fromJson(node);
                    String status = order.getStatus().toUpperCase();

                    switch (status) {
                        // Pendente = aguardando aprovação
                        case "PENDING" ->
                                pendingList.add(order);

                        // Revisadas = qualquer status finalizado (nunca "draft")
                        case "CONFIRMED", "SHIPPED", "RECEIVED", "CANCELLED" ->
                                reviewedList.add(order);

                        // "draft" é ignorado — eram rascunhos que não foram submetidos
                        default -> { /* ignora draft e qualquer status desconhecido */ }
                    }
                }
            }

            Platform.runLater(() -> {
                pendingTable .setItems(FXCollections.observableArrayList(pendingList));
                reviewedTable.setItems(FXCollections.observableArrayList(reviewedList));
            });
        });

        task.setOnFailed(e ->
                System.err.println("Erro ao carregar pedidos: " + task.getException().getMessage())
        );

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}