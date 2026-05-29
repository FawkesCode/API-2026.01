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

import java.util.List;

public class DashboardPageController {
    @FXML private VBox dashboardContainer;
    @FXML private Label lblCount;
    @FXML private Label lblTotalPrice;

    // CAMPOS DA TABELA
    @FXML private TableView<LastOrders> lastOrdersTable;
    @FXML private TableColumn<LastOrders, Integer> columnId;
    @FXML private TableColumn<LastOrders, String> columnSupplier;
    @FXML private TableColumn<LastOrders, String> columnSolicitor;
    @FXML private TableColumn<LastOrders, Double> columnValue;
    @FXML private TableColumn<LastOrders, String> columnStatus;
    @FXML private TableColumn<LastOrders, String> columnDeliveryDate;

    //CONTROLES DE PAGINAÇÃO
    private int currentPage = 0;
    private final int pageSize = 7;
    private boolean isLastPage = true;

    @FXML private Label curPage;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;

    //GRÁFICOS
    @FXML private VBox purchasesPerMonth;
    @FXML private VBox requestStatus;

    @FXML
    public void initialize() {
        loadDashboard();
        tableMapping();
    }

    private void tableMapping() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnId.setCellFactory(column -> new TableCell<LastOrders, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);

                if (empty || id == null) {
                    setText(null);
                } else {
                    String formatedID = String.format("PED-%02d", id);
                    setText(formatedID);
                }
            }
        });
        columnSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        columnSolicitor.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
        columnValue.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        columnValue.setCellFactory(column -> new TableCell<LastOrders, Double>() {
            @Override
            protected void updateItem(Double totalValue, boolean empty) {
                super.updateItem(totalValue, empty);

                if (empty || totalValue == null) {
                    setText(null);
                } else {
                    String value = StringUtils.CURRENCY.format(totalValue);
                    setText(value);
                }
            }
        });
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        columnStatus.setCellFactory(column -> new TableCell<LastOrders, String>() {
            private final Label badgeStatus = new Label();

            {
                badgeStatus.setMaxWidth(Double.MAX_VALUE);
                badgeStatus.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    badgeStatus.getStyleClass().clear();
                    badgeStatus.getStyleClass().add("label");

                    this.setStyle("-fx-padding: 4 12 4 12;");
                    this.setAlignment(Pos.CENTER);

                    LastOrders order = getTableView().getItems().get(getIndex());
                    badgeStatus.setText(order.getStatus());
                    badgeStatus.getStyleClass().add("dashboard__table--status");

                    switch (status) {
                        case "pending" -> {
                            badgeStatus.getStyleClass().add("dashboard__table--pending");
                            badgeStatus.setText("Sob Revisão");
                        }
                        case "confirmed" -> {
                            badgeStatus.getStyleClass().add("dashboard__table--confirmed");
                            badgeStatus.setText("Aprovado");
                        }
                        case "cancelled" -> {
                            badgeStatus.getStyleClass().add("dashboard__table--cancelled");
                            badgeStatus.setText("Negado");
                        }
                        default -> badgeStatus.getStyleClass().add("dashboard__table--default");
                    }

                    setGraphic(badgeStatus);
                }
            }
        });
        columnDeliveryDate.setCellValueFactory(new PropertyValueFactory<>("expectedDeliveryDate"));
    }

    private void loadDashboard() {
        loadLastOrders();
        loadOrdersPrice();
        loadMinProducts();
        loadProblemsOrders();
        loadBarChart();
        pieChart();
        loadRecentActivity();
    }

    private void loadOrdersPrice() {
        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/movimentacao-mensal");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();

            JsonNode purchaseOrdersArray = data.get("purchaseOrders");

            if (purchaseOrdersArray != null && purchaseOrdersArray.isArray() && !purchaseOrdersArray.isEmpty()) {
                JsonNode firstOrderInfo = purchaseOrdersArray.get(0);

                int count = firstOrderInfo.get("count").asInt();
                double totalValue = firstOrderInfo.get("totalValue").asDouble();

                lblCount.setText("em " + count + " compras realizadas e recebidas");
                lblTotalPrice.setText(com.fawkes.front.utils.StringUtils.CURRENCY.format(totalValue));
            } else {
                lblCount.setText("0");
                lblTotalPrice.setText("R$ 0,00");
            }
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadMinProducts() {
        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/produtos-criticos");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadProblemsOrders() {
        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/kpis");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadLastOrders() {
        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                String url = String.format("/dashboard/ultimas-ordens-compra?page=%d&size=%d", currentPage, pageSize);
                return ApiClient.get(url);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode contentArray = data.get("content");
                List<LastOrders> ordersList = mapper.convertValue(
                        contentArray,
                        new TypeReference<List<LastOrders>>() {}
                );
                lastOrdersTable.getItems().setAll(ordersList);

                isLastPage = data.get("last").asBoolean();
                boolean isFirstPage = data.get("first").asBoolean();
                int totalPages = data.get("totalPages").asInt();

                curPage.setText(String.format("Página %d de %d", currentPage + 1, totalPages == 0 ? 1 : totalPages));
                btnPrev.setDisable(isFirstPage);
                btnNext.setDisable(isLastPage);
            } catch (Exception ex) {
                System.err.println("Erro ao mapear os dados da tabela: " + ex.getMessage());
                ex.printStackTrace();
            }
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            loadLastOrders();
        }
    }

    @FXML
    private void handleNextPage() {
        if (!isLastPage) {
            currentPage++;
            loadLastOrders();
        }
    }


    private BarChart<String, Number> createBarChart() {

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mês");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Compras");

        BarChart<String, Number> createBarChart = new BarChart<>(xAxis, yAxis);

        // Create an empty series (will be filled later)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Movimentação Mensal");
        createBarChart.getData().add(series);

        createBarChart.setLegendVisible(false);

        purchasesPerMonth.getChildren().add(createBarChart);
        return createBarChart;
    }

    private void loadBarChart() {
        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/dashboard/movimentacao-mensal");
            }
        };
        System.out.println("FUUUUUUUUUUUUUCK");
//        task.setOnSucceeded(e -> Platform.runLater(() -> {
//            JsonNode data = task.getValue();
//            System.out.println("oieeee");
//            System.out.println(data.toPrettyString());
//        }));
//        task.setOnFailed(e -> Platform.runLater(() -> {
//            System.out.println("Erro ao carregar funcionários: " + task.getException().getMessage());
//        }));


        task.setOnSucceeded(e -> Platform.runLater(() -> {
            JsonNode data = task.getValue();
            XYChart.Series<String, Number> series = createBarChart().getData().get(0);
            series.getData().clear();

            System.out.println("DADOS REAL OFICIAL VAM BORA");
            System.out.println(data.toPrettyString());

            for (JsonNode item : data) {
                String month = item.get("monthLabel").asText();
                double value = item.get("count").asDouble();
                series.getData().add(new XYChart.Data<>(month, value));
            }
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    protected void pieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Aprovados", 10),
                new PieChart.Data("Negados", 5),
                new PieChart.Data("Entregues", 3),
                new PieChart.Data("Devolvidos", 4),
                new PieChart.Data("Atrasados", 2)
        );
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Status dos Pedidos");
        pieChart.setClockwise(true);
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(180);
        pieChart.setLabelsVisible(false);

        requestStatus.getChildren().add(pieChart);
    }

    // ── Atividade Recente ────────────────────────────────────────────────

    @FXML private VBox recentActivityContainer;

    private void loadRecentActivity() {
        if (recentActivityContainer == null) return; // segurança: FXML pode não ter o campo ainda

        recentActivityContainer.getChildren().clear();

        Label loading = new Label("Carregando atividades...");
        recentActivityContainer.getChildren().add(loading);

        Task<com.fasterxml.jackson.databind.JsonNode> task = new javafx.concurrent.Task<>() {
            @Override
            protected com.fasterxml.jackson.databind.JsonNode call() throws Exception {
                return ApiClient.get("/api/stock/movements/activity");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            recentActivityContainer.getChildren().clear();
            com.fasterxml.jackson.databind.JsonNode data = task.getValue();

            if (!data.isArray() || data.isEmpty()) {
                recentActivityContainer.getChildren().add(new Label("Nenhuma atividade registrada."));
                return;
            }

            int limit = Math.min(data.size(), 10);
            for (int i = 0; i < limit; i++) {
                com.fasterxml.jackson.databind.JsonNode node = data.get(i);

                String type         = node.path("type").asText("ENTRADA");
                String productName  = node.path("productName").asText("-");
                int    quantity     = node.path("quantity").asInt(0);
                String dateRaw      = node.path("date").asText("-");

                String icon  = "SAIDA".equals(type) ? "↓" : "↑";
                String color = "SAIDA".equals(type) ? "#e74c3c" : "#2ecc71";

                Label entry = new Label(icon + "  " + productName + "  ×" + quantity + "   " + dateRaw);
                entry.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-padding: 4 0 4 0;");
                entry.setWrapText(true);
                recentActivityContainer.getChildren().add(entry);
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            recentActivityContainer.getChildren().clear();
            recentActivityContainer.getChildren().add(
                    new Label("Erro ao carregar atividades: " + task.getException().getMessage())
            );
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
