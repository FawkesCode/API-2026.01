package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.ProductShopCard;
import com.fawkes.front.components.StockCard;
import com.fawkes.front.components.SupplierCard;
import com.fawkes.front.models.Employee;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.RBACUtil;
import com.fawkes.front.utils.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class StockPageController {
    @FXML private VBox stockContainer;
    @FXML private TextField searchField;

    private ObservableList<JsonNode> allItems = FXCollections.observableArrayList();
    private HashMap<String, Long> productMap = new HashMap<>();
    private static final Long DEFAULT_STOCK_ID = 1L; // Estoque padrão da empresa

    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private List<StockItem> allStockItens = new ArrayList<>();
    private Label statusLabel = new Label();

    @FXML
    public void initialize() {
        loadStock();
    }


    @FXML
    public void loadStock() {
        stockContainer.getChildren().clear();

        Label loading = new Label("Carregando estoque...");
        stockContainer.getChildren().add(loading);

        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/stock");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            stockContainer.getChildren().clear();
            JsonNode data = task.getValue();

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhum produto cadastrado no estoque.");
                return;
            }

            allStockItens.clear();
            for (JsonNode node: data) {
                allStockItens.add(StockItem.fromJson(node));
            }

            renderStockGroup(allStockItens);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            stockContainer.getChildren().clear();
            setErrorMessage("Erro ao carregar estoque: " + task.getException().getMessage());
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    public void renderStockGroup(List<StockItem> products) {
        stockContainer.getChildren().clear();

        java.util.Map<Boolean, List<StockItem>> groups = products.stream()
                .collect(java.util.stream.Collectors.groupingBy(StockItem::isLow));

        List<Boolean> order = List.of(true, false);

        for (Boolean isLowGroup : order) {
            List<StockItem> subList = groups.getOrDefault(isLowGroup, new ArrayList<>());

            if (subList.isEmpty()) continue;

            String title = isLowGroup ? "Itens que precisam de atenção" : "Itens disponíveis em boa quantidade";
            Label groupLabel = new Label(title);
            groupLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37404C; "
                    + "-fx-padding: 16px 0px 8px 0px;");

            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            flow.setAlignment(Pos.TOP_LEFT);

            List<StockItem> sortedSubList = subList.stream().sorted((p1, p2) -> {
                if (p1.getCurrentStockQuantity() == 0 && p2.getCurrentStockQuantity() > 0) return 1;
                if (p1.getCurrentStockQuantity() > 0 && p2.getCurrentStockQuantity() == 0) return -1;
                return 0;
            }).toList();

            for (StockItem product : sortedSubList) {
                StockCard card = new StockCard();
                card.setData(product);
                card.setOnEditAction(this::openViewProduct);
                flow.getChildren().add(card);
            }

            stockContainer.getChildren().addAll(groupLabel, flow);
        }
    }

    private void setErrorMessage (String message) {
        stockContainer.getChildren().clear();
        statusLabel.setText(message);
        stockContainer.getChildren().add(statusLabel);
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            renderStockGroup(allStockItens);
            return;
        }

        List<StockItem> filtered = allStockItens.stream().filter(pro -> pro.getProductName().toLowerCase().contains(query)).toList();

        renderStockGroup(filtered);

        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum produto encontrado.");
        }
    }

    private void openViewProduct(StockItem pro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/see-stockItem-form.fxml"));
            ViewProductForm controller = new ViewProductForm();
            loader.setController(controller);
            Parent formulario = loader.load();
            controller.setProductData(pro);
            Stage curStage = ((Stage) stockContainer.getScene().getWindow());
            ModalManager.openModal(curStage, formulario, "Informações sobre o produto " + pro.getProductName(), 600, 400, "ModalFrameSM.fxml", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleOpenInputDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-stockItem-form.fxml"));
        Parent formulario = loader.load();
        AddStockItemForm controller = loader.getController();
        controller.setOnSaveSuccess(this::loadStock);
        Stage curStage = ((Stage) searchField.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Produto");
    }

    @FXML
    public void handleOpenOutputDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-stockItemExit-form.fxml"));
        Parent formulario = loader.load();
        ExitStockItemForm controller = loader.getController();
        controller.setOnSaveSuccess(this::loadStock);
        Stage curStage = ((Stage) searchField.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Registrar Saída", 600, 400, "ModalFrameSM.fxml", true);
    }

}
