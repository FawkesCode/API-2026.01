package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.ProductSupplierCard;
import com.fawkes.front.components.StockCard;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.models.Supplier;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.NavigationManager;
import com.fawkes.front.utils.RBACUtil;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductsPageController {

    @FXML private JFXButton btnInput;
    @FXML private VBox productsContainer;
    @FXML private TextField searchField;
    private NavigationManager nm = NavigationManager.getInstance();
    private Label statusLabel = new Label();
    private List<StockItem> allProducts = new ArrayList<>();

    public void initialize() {
        applyRBACRestrictions();
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

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhum produto cadastrado no estoque.");
                return;
            }

            allProducts.clear();
            for (JsonNode node: data) {
                allProducts.add(StockItem.fromJson(node));
            }


            renderStockGroup(allProducts);
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
    public void renderStockGroup(List<StockItem> products) {
        productsContainer.getChildren().clear();

        java.util.Map<String, java.util.List<StockItem>> byGroup = new java.util.LinkedHashMap<>();

        for (StockItem pro: products) {
            byGroup.computeIfAbsent(pro.getSupplierName(), k -> new ArrayList<>()).add(pro);
        }

        for (java.util.Map.Entry<String, java.util.List<StockItem>> entry : byGroup.entrySet()) {
            Label groupLabel = new Label("Produtos disponibilizados pelo " + (entry.getKey()));
            groupLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37404C; "
                    + "-fx-padding: 16px 0px 8px 0px;");

            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            flow.setAlignment(Pos.TOP_LEFT);

            List<StockItem> sortedProducts = entry.getValue().stream().sorted((p1, p2)-> {
                if(p1.getCurrentStockQuantity() == 0 && p2.getCurrentStockQuantity() > 0) return 1;
                if(p1.getCurrentStockQuantity() > 0 && p2.getCurrentStockQuantity() == 0) return -1;
                return 0;
            }).toList();

            for (StockItem product: sortedProducts) {
                ProductSupplierCard card = new ProductSupplierCard();
                card.setData(product);
                card.setOnEditAction(this::openEditProduct);
                card.setOnDeleteAction(this::confirmDeleteProduct);

                flow.getChildren().add(card);

            }

            productsContainer.getChildren().addAll(groupLabel, flow);
        }

    }


    private void setErrorMessage (String message) {
        productsContainer.getChildren().clear();
        statusLabel.setText(message);
        productsContainer.getChildren().add(statusLabel);
    }

    private void openEditProduct(StockItem pro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-stockItem-form.fxml"));
            EditStockItemForm controller = new EditStockItemForm();
            loader.setController(controller);
            Parent formulario = loader.load();
            controller.setProductData(pro);
            controller.setOnSaveSuccess(this::loadStock);
            Stage curStage = ((Stage) productsContainer.getScene().getWindow());
            ModalManager.openModal(curStage, formulario, "Informações sobre o produto " + pro.getProductName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmDeleteProduct(StockItem pro) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja excluir o produto " + pro.getProductName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                Task<Void> deleteWeight = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        ApiClient.delete("/api/products/" + pro.getProductId());
                        return null;
                    }
                };

                deleteWeight.setOnSucceeded(ev -> {
                    Platform.runLater(() -> {
                        System.out.println("Deletado com sucesso!");
                        loadStock();
                    });
                });

                deleteWeight.setOnFailed(ev -> {
                    Throwable e = deleteWeight.getException();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao deletar: " + e.getMessage());
                        alert.show();
                    });
                });

                new Thread(deleteWeight).start();
            }
        });
    }



    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            renderStockGroup(allProducts);
            return;
        }

        List<StockItem> filtered = allProducts.stream().filter(pro -> pro.getProductName().toLowerCase().contains(query) || pro.getSupplierName().toLowerCase().contains(query) ).toList();

        renderStockGroup(filtered);

        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum fornecedor encontrado.");
        }
    }

    @FXML
    public void handleAddSupplier() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-stockItem-form.fxml"));
        AddStockItemForm controller = new AddStockItemForm();
        loader.setController(controller);
        controller.setOnSaveSuccess(this::loadStock);
        Parent formulario = loader.load();
        Stage curStage = ((Stage) btnInput.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Produto");
    }

    @FXML
    public void handleBackButton() throws IOException {
        StackPane container = (StackPane) productsContainer.getScene().getRoot().lookup("#container");
        nm.navigateToPage(container, "view/suppliers-page.fxml", "Fornecedores", "Aqui é onde você e outros gerentes podem administrar os fornecedores cadastrados na plataforma.");
    }

}
