package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fawkes.front.models.FormProducts;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.fawkes.front.utils.NavigationManager;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class NewRequestForm {
    @FXML private JFXButton btnApprove;
    @FXML private JFXButton btnDecline;
    @FXML private TextField departmentField;
    @FXML private Label description;
    @FXML private TextArea descriptionField;
    @FXML private Label districtName;
    @FXML private ComboBox<String> paymentField;
    @FXML private Label paymentMethod;
    @FXML private VBox productsContainer;
    @FXML private Label solicitorName;
    @FXML private VBox suppliersContainer;
    @FXML private Label totalPrice;
    @FXML private Label totalQuantity;
    @FXML private Label messageLabel;

    List<FormProducts> products;
    int requestQtd;
    double requestPrice;
    List<String> suppliers;
    NavigationManager nm = NavigationManager.getInstance();

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    UserInfoManager loggedUser = UserInfoManager.getInstance();

    @FXML
    public void initialize() {
        solicitorName.setText(loggedUser.getUserName() + " | " + StringUtils.roleTranslation(loggedUser.getUserRole()));
    }

    public void setData(List<FormProducts> productsList, int qtd, double price, List<String> suppliersList) {
        this.products = productsList;
        this.requestQtd = qtd;
        this.requestPrice = price;
        this.suppliers = suppliersList;


        renderProductsData();
    }

    private void renderProductsData() {
        productsContainer.getChildren().clear();
        suppliersContainer.getChildren().clear();

        for (String s: suppliers) {
            Label supplier = new Label(s);
            supplier.getStyleClass().add("input__label--info");

            suppliersContainer.getChildren().add(supplier);
        }

        for (FormProducts p : products) {
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

            productsContainer.getChildren().add(productsLineContainer);

        }

        totalPrice.setText("Total: " + CURRENCY.format(requestPrice));
        totalQuantity.setText("Qtd. de Itens: " + requestQtd);
    }


    public void handleSubmmit() {
        Map<Integer, List<FormProducts>> productsPerSupplier = products.stream().collect(Collectors.groupingBy(FormProducts::getSuppliersId));

        showError("Enviando pedidos...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String userId = UserInfoManager.getInstance().getUserId();
                ObjectMapper mapper = new ObjectMapper();

                for (Map.Entry<Integer, List<FormProducts>> entry : productsPerSupplier.entrySet()) {
                    int supplierId = entry.getKey();
                    List<FormProducts> supplierItems = entry.getValue();

                    ObjectNode draftNode = mapper.createObjectNode();
                    draftNode.put("supplierId", supplierId);
                    draftNode.put("userId", Long.parseLong(userId));

                    JsonNode draft = ApiClient.post("/api/purchase-orders/draft",
                            mapper.writeValueAsString(draftNode));
                    Long orderId = draft.path("id").asLong();

                    for (FormProducts p : supplierItems) {
                        ObjectNode itemNode = mapper.createObjectNode();
                        itemNode.put("productId", p.getId());
                        itemNode.put("quantity", p.getQuantity());
                        itemNode.put("unitPrice", p.getUnityPriceValue());

                        ApiClient.post("/api/purchase-orders/" + orderId + "/items",
                                mapper.writeValueAsString(itemNode));
                    }

                    ApiClient.post("/api/purchase-orders/" + orderId + "/submit", "{}");
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            Stage modalStage = (Stage) totalQuantity.getScene().getWindow();
            modalStage.close();

            Stage primaryStage = (Stage) javafx.stage.Window.getWindows().stream()
                    .filter(w -> w instanceof Stage && w != modalStage)
                    .findFirst()
                    .orElse(null);

            if (primaryStage != null) {
                StackPane container = (StackPane) primaryStage.getScene().getRoot().lookup("#container");
                if (container != null) {
                    nm.navigateToPage(container, "view/orders-page.fxml", "Pedidos de Compras", "Onde você e outros funcionários podem vizualizar os pedidos em aberto ou finalizados.");
                }
            }
            products.clear();
            suppliers.clear();
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            showError("Erro: " + task.getException().getMessage());
            task.getException().printStackTrace();
        }));

        new Thread(task).start();
    }

    private void showError(String msg) { Platform.runLater(() -> messageLabel.setText(msg)); }
    @FXML
    private void handleCloseModal() {
        ((Stage) btnApprove.getScene().getWindow()).close();
    }
}
