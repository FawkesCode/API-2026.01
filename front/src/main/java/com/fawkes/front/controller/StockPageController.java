package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.models.Employee;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.RBACUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class StockPageController {

    @FXML private TableView<JsonNode>          stockTable;
    @FXML private TableColumn<JsonNode, String> colName;
    @FXML private TableColumn<JsonNode, String> colType;
    @FXML private TableColumn<JsonNode, String> colUnit;
    @FXML private TableColumn<JsonNode, String> colValue;
    @FXML private TableColumn<JsonNode, String> colCurrent;
    @FXML private TableColumn<JsonNode, String> colMin;
    @FXML private TableColumn<JsonNode, String> colMax;
    @FXML private TextField searchField;
    @FXML private Button btnInput;
    @FXML private Button btnOutput;

    @FXML private VBox      inputDialog;
    @FXML private TextField inputStockId;
    @FXML private TextField inputProductId;
    @FXML private TextField inputQuantity;
    @FXML private Label     inputErrorLabel;

    // --- Diálogo de Saída ---
    @FXML private VBox      outputDialog;
    @FXML private ComboBox<String> outputProductCombo;
    @FXML private TextField outputQuantity;
    @FXML private Label     outputErrorLabel;

    @FXML private VBox stockContainer;

    private ObservableList<JsonNode> allItems = FXCollections.observableArrayList();
    private HashMap<String, Long> productMap = new HashMap<>();
    private static final Long DEFAULT_STOCK_ID = 1L; // Estoque padrão da empresa

    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private List<StockItem> allStockItens = new ArrayList<>();
    private Label statusLabel = new Label();

    @FXML
    public void initialize() {
        // Apply RBAC restrictions based on user role
        applyRBACRestrictions();

//        colName.setCellValueFactory(d ->
//                new SimpleStringProperty(d.getValue().path("productName").asText("-")));
//        colType.setCellValueFactory(d ->
//                new SimpleStringProperty(d.getValue().path("productType").asText("-")));
//        colUnit.setCellValueFactory(d ->
//                new SimpleStringProperty(d.getValue().path("measurementUnit").asText("-")));
//        colValue.setCellValueFactory(d -> {
//            double v = d.getValue().path("unitValue").asDouble(0);
//            return new SimpleStringProperty(CURRENCY_FMT.format(v));
//        });
//        colCurrent.setCellValueFactory(d ->
//                new SimpleStringProperty(d.getValue().path("currentStockQuantity").asText("-")));
//        colMin.setCellValueFactory(d ->
//                new SimpleStringProperty(d.getValue().path("minStockQuantity").asText("-")));
//        colMax.setCellValueFactory(d ->
//                new SimpleStringProperty(d.getValue().path("maxStockQuantity").asText("-")));

        /*stockTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(JsonNode item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    int current = item.path("currentStockQuantity").asInt(Integer.MAX_VALUE);
                    int min     = item.path("minStockQuantity").asInt(0);
                    setStyle(current <= min ? "-fx-background-color: #fff0f0;" : "");
                }
            }
        });*/

        loadStock();
    }

    private void applyRBACRestrictions() {
        // OPERATIONAL users cannot create new products, only register output
        if (!RBACUtil.canManageProducts()) {
            btnInput.setVisible(false);
            btnInput.setManaged(false);
        }
    }

    @FXML
    public void loadStock() {
        stockContainer.getChildren().clear();

        try {
            JsonNode data = ApiClient.get("/api/stock");

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhum produto cadastrado.");
                return;
            }

            allStockItens.clear();
            for (JsonNode node: data) {
                allStockItens.add(StockItem.fromJson(node));
            }

//            renderStockGroup();

        } catch (Exception e) {
            setErrorMessage("Erro ao carregar estoque: " + e.getMessage());
        }

    }

    @FXML
    public void renderStockGroup(List<StockItem> products) {
        stockContainer.getChildren().clear();

        java.util.Map<String, java.util.List<Employee>> byGroup = new java.util.LinkedHashMap<>();

//        for (StockItem pro : products) {
//            byGroup.computeIfAbsent(pro.get(), k -> new ArrayList<>()).add(emp);
//        }

    }

    private void setErrorMessage (String message) {
        stockContainer.getChildren().clear();
        statusLabel.setText(message);
        stockContainer.getChildren().add(statusLabel);
    }

//    @FXML
//    public void loadStock() {
//        statusLabel.setText("Carregando...");
//        try {
//            JsonNode data = ApiClient.listStock();
//            allItems.clear();
//            for (JsonNode item : data) {
//                allItems.add(item);
//            }
//            stockTable.setItems(allItems);
//            statusLabel.setText("Estoque carregado. " + allItems.size() + " item(ns).");
//        } catch (Exception e) {
//            statusLabel.setText("Erro ao carregar estoque: " + e.getMessage());
//        }
//    }

//    @FXML
//    public void handleSearch() {
//        String query = searchField.getText().trim().toLowerCase();
//        if (query.isEmpty()) {
//            stockTable.setItems(allItems);
//            return;
//        }
//        ObservableList<JsonNode> filtered = FXCollections.observableArrayList();
//        for (JsonNode item : allItems) {
//            if (item.path("productName").asText("").toLowerCase().contains(query) ||
//                    item.path("productType").asText("").toLowerCase().contains(query)) {
//                filtered.add(item);
//            }
//        }
//        stockTable.setItems(filtered);
//    }

    @FXML
    public void handleOpenInputDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-stockItem-form.fxml"));
        Parent formulario = loader.load();
        AddStockItemForm controller = loader.getController();
        controller.setOnSaveSuccess(this::loadStock);
        Stage curStage = ((Stage) btnInput.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Produto");
    }

    @FXML
    public void handleOpenOutputDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-stockItemExit-form.fxml"));
        Parent formulario = loader.load();
        ExitStockItemForm controller = loader.getController();
        controller.setOnSaveSuccess(this::loadStock);
        Stage curStage = ((Stage) btnOutput.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Registrar Saída", 600, 400, "ModalFrameSM.fxml", true);
    }

}
