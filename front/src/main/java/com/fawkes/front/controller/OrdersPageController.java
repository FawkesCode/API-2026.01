package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.EmployeeCard;
import com.fawkes.front.components.OrdersCard;
import com.fawkes.front.models.Employee;
import com.fawkes.front.models.Order;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.NavigationManager;
import com.fawkes.front.utils.RBACUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersPageController {

    @FXML private VBox pageContent;
    @FXML private VBox requestsContainer;
    @FXML private TextField searchField;

    // Status
    @FXML private Label pendingRequestsLabel;
    @FXML private Label aprovedRequestsLabell;
    @FXML private Label declinedRequestsLabel;

    NavigationManager nm = NavigationManager.getInstance();
    private static final NumberFormat CURRENCY_FMT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private List<Order> allRequests = new ArrayList<>();

    @FXML
    public void initialize() {
        loadOrders();
        requestsContainer.setMinWidth(0);
        requestsContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        requestsContainer.setMaxWidth(Double.MAX_VALUE);
    }



    private void setErrorMessage(String message) {
        requestsContainer.getChildren().clear();
        Label statusLabel = new Label(message);
        requestsContainer.getChildren().add(statusLabel);
    }

    @FXML
    private void loadOrders() {
        requestsContainer.getChildren().clear();
        List<Order> pendingOrders = new ArrayList<>();
        List<Order> aprovedOrders = new ArrayList<>();
        List<Order> declinedOrders = new ArrayList<>();

        Label loading = new Label("Carregando Pedidos...");
        requestsContainer.getChildren().add(loading);

        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/purchase-orders");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            requestsContainer.getChildren().clear();
            JsonNode data = task.getValue();

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhum pedido foi realizado ainda.");
                return;
            }

            allRequests.clear();
            for (JsonNode node: data) {
                allRequests.add(Order.fromJson(node));
            }

            System.out.println("DADOS DA API:" + data.toPrettyString());


            for (JsonNode node: data) {
                Order ord = Order.fromJson(node);
                String status = ord.getStatus().toLowerCase();

                if (status.toLowerCase().equals("pending")) {
                    pendingOrders.add(ord);
                } else if (status.toLowerCase().equals("confirmed")) {
                    aprovedOrders.add(ord);
                } else {
                    declinedOrders.add(ord);
                }
            }

            int qtdPendingOrders = pendingOrders.toArray().length;
            int qtdAprovedOrders = aprovedOrders.toArray().length;
            int qtdDeclinedOrders = declinedOrders.toArray().length;

            renderOrders(allRequests);

            pendingRequestsLabel.setText(qtdPendingOrders + " Pendentes");
            aprovedRequestsLabell.setText(qtdAprovedOrders + " Aprovados");
            declinedRequestsLabel.setText(qtdDeclinedOrders + " Recusados");
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            requestsContainer.getChildren().clear();
            setErrorMessage("Erro ao carregar funcionários: " + task.getException().getMessage());
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    private void renderOrders(List<Order> orders) {
        requestsContainer.getChildren().clear();

        java.util.Map<String, java.util.List<Order>> byGroup = new java.util.LinkedHashMap<>();

        byGroup.put("PENDING", new ArrayList<>());
        byGroup.put("REVIEWED", new ArrayList<>());

        for (Order ord : orders) {
            String status = ord.getStatus().toLowerCase();

            if (status.equals("pending")) {
                byGroup.get("PENDING").add(ord);
            } else {
                byGroup.get("REVIEWED").add(ord);
            }
        }

        for (java.util.Map.Entry<String, java.util.List<Order>> entry : byGroup.entrySet()) {
            if (entry.getValue().isEmpty()) continue;

            String title = entry.getKey().equals("PENDING") ? "Solicitações de Compra" : "Compras já revisadas";
            Label groupLabel = new Label(title);
            groupLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37404C; "
                    + "-fx-padding: 16px 0px 8px 0px;");

            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            flow.getChildren().add(groupLabel);

            for (Order order : entry.getValue()) {
                OrdersCard card = new OrdersCard();
                card.setData(order);
                card.setOnViewAction(this::openPendingModal);
                card.prefWidthProperty().bind(requestsContainer.widthProperty());

                flow.getChildren().add(card);
            }
            requestsContainer.getChildren().add(flow);
        }
    }

    private void openPendingModal(Order ord) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/pending-request-form.fxml"));
            Parent formulario = loader.load();
            PendingRequestForm controller = loader.getController();
            Stage curStage = ((Stage) requestsContainer.getScene().getWindow());
            controller.setData(ord, curStage);
            controller.setOnSaveSuccess(this::loadOrders);
            ModalManager.openModal(curStage, formulario, "Visualizar Pedido " + ord.getId(), 700.0, 400.0, "ModalFrameM.fxml", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNewRequest() {
        StackPane container = (StackPane) requestsContainer.getScene().getRoot().lookup("#container");
        nm.navigateToPage(container, "view/request-order-page.fxml", "Pedidos de Compra >> Solicitar Pedido", "Selecione os produtos para adioná-los ao carrinho e prosseguir com a solicitação do pedido.");
    }

    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        requestsContainer.getChildren().clear();
        if (query.isEmpty()) {
            renderOrders(allRequests);
            return;
        }

        List<Order> filtered = allRequests.stream().filter(ord ->
                (ord.getStatus() != null && ord.getStatus().toLowerCase().contains(query)) ||
                        (ord.getSector() != null && ord.getSector().toLowerCase().contains(query)) ||
                        (ord.getRequesterName() != null && ord.getRequesterName().toLowerCase().contains(query))
        ).toList();

        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum pedido encontrado.");
        } else {
            renderOrders(filtered);
        }


    }


}