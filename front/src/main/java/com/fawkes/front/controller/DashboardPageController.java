package com.fawkes.front.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawkes.front.models.LastOrders;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardPageController {

    @FXML private VBox dashboardContainer;
    @FXML private Label lblCount;
    @FXML private Label lblTotalPrice;
    @FXML private ComboBox<String> supplierFilter;

    @FXML private Label kpiPending;
    @FXML private Label kpiConfirmed;
    @FXML private Label kpiShipped;
    @FXML private Label kpiReceived;

    @FXML private VBox minProductsContainer;
    @FXML private VBox problemOrdersContainer;

    @FXML private TableView<LastOrders> lastOrdersTable;
    @FXML private TableColumn<LastOrders, Integer> columnId;
    @FXML private TableColumn<LastOrders, String>  columnSupplier;
    @FXML private TableColumn<LastOrders, String>  columnSolicitor;
    @FXML private TableColumn<LastOrders, Double>  columnValue;
    @FXML private TableColumn<LastOrders, String>  columnStatus;

    @FXML private Label  curPage;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;

    @FXML private VBox purchasesPerMonth;
    @FXML private VBox requestStatus;

    private int  currentPage = 0;
    private final int pageSize = 7;
    private boolean isLastPage = true;
    private Long selectedSupplierId = null;

    private final Map<String, Long> supplierMap = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        tableMapping();
        loadSuppliers();
        loadDashboard();
    }

    // ── Fornecedores ─────────────────────────────────────────────────
    private void loadSuppliers() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/api/suppliers");
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
            supplierMap.clear();
            supplierFilter.getItems().clear();
            supplierFilter.getItems().add("Todos");
            if (data.isArray()) {
                for (JsonNode node : data) {
                    long id   = node.get("id").asLong();
                    String name = node.get("supplierName").asText();
                    supplierMap.put(name, id);
                    supplierFilter.getItems().add(name);
                }
            }
            supplierFilter.setValue("Todos");
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }

    @FXML
    private void handleSupplierFilter() {
        String selected = supplierFilter.getValue();
        if (selected == null || selected.equals("Todos")) {
            selectedSupplierId = null;
        } else {
            selectedSupplierId = supplierMap.get(selected);
        }
        currentPage = 0;
        loadLastOrders();
    }

    private void loadKpis() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/kpis");
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
            kpiPending.setText(String.valueOf(
                    data.path("purchaseOrdersPending").path("value").asInt()));
            kpiConfirmed.setText(String.valueOf(
                    data.path("purchaseOrdersConfirmed").path("value").asInt()));
            kpiShipped.setText(String.valueOf(
                    data.path("purchaseOrdersShipped").path("value").asInt()));
            kpiReceived.setText(String.valueOf(
                    data.path("purchaseOrdersReceived").path("value").asInt()));
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }

    // ── Dashboard geral ───────────────────────────────────────────────
    private void loadDashboard() {
        loadLastOrders();
        loadOrdersPrice();
        loadBarChart();
        loadPieChart();
        loadKpis();
        loadMinProducts();
        loadProblemsOrders();
    }

    // ── Produtos críticos ─────────────────────────────────────────────
    private void loadMinProducts() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/produtos-criticos");
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
            minProductsContainer.getChildren().clear();

            JsonNode lowStock = data.get("lowStock");
            if (lowStock != null && lowStock.isArray() && !lowStock.isEmpty()) {
                for (JsonNode item : lowStock) {
                    String name = item.path("productName").asText("Produto");
                    int qty     = item.path("availableQuantity").asInt(0);
                    int min     = item.path("minStockQuantity").asInt(0);

                    Label lbl = new Label(name + " → " + qty + " restantes (mín: " + min + ")");
                    lbl.setMaxWidth(Double.MAX_VALUE);
                    lbl.getStyleClass().add("dashboard__alerts--products");
                    minProductsContainer.getChildren().add(lbl);
                }
            } else {
                Label lbl = new Label("Nenhum produto abaixo do mínimo");
                lbl.getStyleClass().add("dashboard__alerts--info");
                minProductsContainer.getChildren().add(lbl);
            }
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }

    // ── Alertas / Complicações ────────────────────────────────────────
    private void loadProblemsOrders() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/alertas");
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
            problemOrdersContainer.getChildren().clear();

            JsonNode orders = data.get("orders");
            if (orders != null && orders.isArray() && !orders.isEmpty()) {
                for (JsonNode alert : orders) {
                    String title = alert.path("title").asText("");
                    String desc  = alert.path("description").asText("");

                    VBox card = new VBox();
                    card.getStyleClass().add("dashboard__alerts--orders-container");

                    Label titleLbl = new Label(title);
                    titleLbl.getStyleClass().add("dashboard__alerts--orders-title");

                    Label descLbl = new Label(desc);
                    descLbl.getStyleClass().add("dashboard__alerts--orders-subtitle");

                    card.getChildren().addAll(titleLbl, descLbl);
                    problemOrdersContainer.getChildren().add(card);
                }
            } else {
                Label lbl = new Label("Nenhuma complicação no momento");
                lbl.getStyleClass().add("dashboard__alerts--info");
                problemOrdersContainer.getChildren().add(lbl);
            }
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }

    // ── Tabela ────────────────────────────────────────────────────────
    private void tableMapping() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnId.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                setText(empty || id == null ? null : String.format("PED-%02d", id));
            }
        });

        columnSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        columnSolicitor.setCellValueFactory(new PropertyValueFactory<>("requesterName"));

        columnValue.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        columnValue.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : StringUtils.CURRENCY.format(v));
            }
        });

        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        columnStatus.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            { badge.setMaxWidth(Double.MAX_VALUE); badge.setAlignment(Pos.CENTER); }

            @Override protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setGraphic(null); return; }

                badge.getStyleClass().clear();
                badge.getStyleClass().addAll("label", "dashboard__table--status");
                setStyle("-fx-padding: 4 12 4 12;");
                setAlignment(Pos.CENTER);

                switch (status) {
                    case "draft"     -> { badge.getStyleClass().add("dashboard__table--pending");   badge.setText("Rascunho");    }
                    case "pending"   -> { badge.getStyleClass().add("dashboard__table--pending");   badge.setText("Sob Revisão"); }
                    case "confirmed" -> { badge.getStyleClass().add("dashboard__table--confirmed"); badge.setText("Aprovado");    }
                    case "shipped"   -> { badge.getStyleClass().add("dashboard__table--confirmed"); badge.setText("Enviado");     }
                    case "received"  -> { badge.getStyleClass().add("dashboard__table--confirmed"); badge.setText("Recebido");    }
                    case "cancelled" -> { badge.getStyleClass().add("dashboard__table--cancelled"); badge.setText("Negado");      }
                    default          ->   badge.getStyleClass().add("dashboard__table--default");
                }
                setGraphic(badge);
            }
        });
    }

    private void loadLastOrders() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                StringBuilder url = new StringBuilder(
                        String.format("/dashboard/ultimas-ordens-compra?page=%d&size=%d", currentPage, pageSize));
                if (selectedSupplierId != null)
                    url.append("&supplierId=").append(selectedSupplierId);
                return ApiClient.get(url.toString());
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<LastOrders> list = mapper.convertValue(
                        data.get("content"), new TypeReference<List<LastOrders>>() {});
                lastOrdersTable.getItems().setAll(list);

                isLastPage = data.get("last").asBoolean();
                int totalPages = data.get("totalPages").asInt();
                curPage.setText(String.format("Página %d de %d", currentPage + 1,
                        totalPages == 0 ? 1 : totalPages));
                btnPrev.setDisable(data.get("first").asBoolean());
                btnNext.setDisable(isLastPage);
            } catch (Exception ex) { ex.printStackTrace(); }
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }

    @FXML private void handlePrevPage() {
        if (currentPage > 0) { currentPage--; loadLastOrders(); }
    }

    @FXML private void handleNextPage() {
        if (!isLastPage) { currentPage++; loadLastOrders(); }
    }

    // ── Painel Atenção ────────────────────────────────────────────────
    private void loadOrdersPrice() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/movimentacao-mensal");
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode arr = task.getValue().get("purchaseOrders");
            if (arr != null && arr.isArray() && !arr.isEmpty()) {
                int count = 0; double total = 0;
                for (JsonNode item : arr) {
                    count += item.get("count").asInt();
                    total += item.get("totalValue").asDouble();
                }
                lblCount.setText("em " + count + " compras realizadas e recebidas");
                lblTotalPrice.setText(StringUtils.CURRENCY.format(total));
            } else {
                lblCount.setText("0");
                lblTotalPrice.setText("R$ 0,00");
            }
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }

    // ── Gráfico de barras ─────────────────────────────────────────────
    private void loadBarChart() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/movimentacao-mensal");
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            purchasesPerMonth.getChildren().removeIf(n -> n instanceof BarChart);

            CategoryAxis xAxis = new CategoryAxis(); xAxis.setLabel("Mês");
            NumberAxis   yAxis = new NumberAxis();   yAxis.setLabel("Compras");
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setLegendVisible(false);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            JsonNode arr = task.getValue().get("purchaseOrders");
            if (arr != null && arr.isArray()) {
                for (JsonNode item : arr)
                    series.getData().add(new XYChart.Data<>(
                            item.get("monthLabel").asText(),
                            item.get("count").asDouble()));
            }
            chart.getData().add(series);
            purchasesPerMonth.getChildren().add(chart);
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }

    // ── Gráfico de pizza ──────────────────────────────────────────────
    private void loadPieChart() {
        Task<JsonNode> task = new Task<>() {
            @Override protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/status-pedidos");
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
            JsonNode arr  = data.get("purchaseOrders");
            if (arr == null || !arr.isArray() || arr.isEmpty()) arr = data.get("orders");

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            if (arr != null && arr.isArray())
                for (JsonNode item : arr) {
                    long count = item.get("count").asLong();
                    if (count > 0)
                        pieData.add(new PieChart.Data(item.get("statusLabel").asText(), count));
                }

            PieChart pie = new PieChart(pieData);
            pie.setTitle("Status dos Pedidos");
            pie.setClockwise(true);
            pie.setLabelsVisible(false);
            pie.setStartAngle(180);

            requestStatus.getChildren().removeIf(n -> n instanceof PieChart);
            requestStatus.getChildren().add(pie);
        }));
        new Thread(task) {{ setDaemon(true); }}.start();
    }
}