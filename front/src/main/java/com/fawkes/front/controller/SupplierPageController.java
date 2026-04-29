package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.components.SupplierCard;
import com.fawkes.front.models.Employee;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.models.Supplier;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.NavigationManager;
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
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SupplierPageController {

    // Tabela
    @FXML private TableView<JsonNode>           supplierTable;
    @FXML private TableColumn<JsonNode, String> colId;
    @FXML private TableColumn<JsonNode, String> colNome;
    @FXML private TableColumn<JsonNode, String> colCnpj;
    @FXML private TableColumn<JsonNode, String> colPagamento;
    @FXML private TableColumn<JsonNode, String> colAcoes;

    // Barra superior
    @FXML private TextField searchField;
    @FXML private Button    btnNewSupplier;
    @FXML private VBox suppliersContainer;
    @FXML private Label activeSuppliersLabel;
    @FXML private Label inactiveSuppliersLabel;

    // Dialog de cadastro
    @FXML private VBox      addDialog;
    @FXML private TextField fieldNome;
    @FXML private TextField fieldCnpj;
    @FXML private ComboBox<String> comboPagamento;
    @FXML private Label     addErrorLabel;

    private final ObservableList<JsonNode> allRows = FXCollections.observableArrayList();
    private List<Supplier> allSuppliers = new ArrayList<>();
    private Label statusLabel = new Label();

    @FXML
    public void initialize() {
        // Apply RBAC restrictions based on user role
        applyRBACRestrictions();

        suppliersContainer.setMinWidth(0);
        suppliersContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        suppliersContainer.setMaxWidth(Double.MAX_VALUE);

        loadSuppliers();
    }

    private void applyRBACRestrictions() {
        // Only DIRECTOR and MANAGER can create new suppliers
        if (!RBACUtil.canManageSuppliers()) {
            btnNewSupplier.setVisible(false);
            btnNewSupplier.setManaged(false);
        }
    }

    private void setErrorMessage(String message) {
        suppliersContainer.getChildren().clear();
        statusLabel.setText(message);
        suppliersContainer.getChildren().add(statusLabel);
    }

    @FXML
    public void loadSuppliers() {
        suppliersContainer.getChildren().clear();
        List<Supplier> activeSuppliers = new ArrayList<>();
        List<Supplier> inactiveSuppliers = new ArrayList<>();

        Label loading = new Label("Carregando fornecedores...");
        suppliersContainer.getChildren().add(loading);

        Task<JsonNode> task = new Task<>() {
            @Override
            protected JsonNode call() throws Exception {
                return ApiClient.get("/api/suppliers");
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            suppliersContainer.getChildren().clear();
            JsonNode data = task.getValue();

            if (!data.isArray() || data.isEmpty()) {
                setErrorMessage("Nenhum produto cadastrado no estoque.");
                return;
            }

            allSuppliers.clear();
            for (JsonNode node: data) {
                allSuppliers.add(Supplier.fromJson(node));
            }

            for (JsonNode node: data) {
                Supplier sup = Supplier.fromJson(node);
                String status = sup.getActive().toLowerCase();

                if (status.equals("ativo")) {
                    activeSuppliers.add(sup);
                } else {
                    inactiveSuppliers.add(sup);
                }
            }

            int qtdActiveUsers = activeSuppliers.toArray().length;
            int qtdInactiveUsers = inactiveSuppliers.toArray().length;

            renderSuppliersGroup(allSuppliers);

            activeSuppliersLabel.setText(qtdActiveUsers + " Ativos");
            inactiveSuppliersLabel.setText(qtdInactiveUsers + " Inativos");

            for (JsonNode node: data) {
                System.out.println(node);
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            suppliersContainer.getChildren().clear();
            setErrorMessage("Erro ao carregar fornecedores: " + task.getException().getMessage());
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void renderSuppliersGroup(List<Supplier> suppliers) {
        suppliersContainer.getChildren().clear();

        java.util.Map<String, java.util.List<Supplier>> byGroup = new java.util.LinkedHashMap<>();

        for (Supplier sup: suppliers) {
            byGroup.computeIfAbsent(sup.getPaymentMethod(), k -> new ArrayList<>()).add(sup);
        }

        for (java.util.Map.Entry<String, java.util.List<Supplier>> entry : byGroup.entrySet()) {
            Label groupLabel = new Label("Fornecedores que aceitam pagamento por " + StringUtils.paymentTranslation(entry.getKey().toUpperCase()).toLowerCase());
            groupLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #37404C; "
                    + "-fx-padding: 16px 0px 8px 0px;");

            FlowPane flow = new FlowPane();
            flow.setHgap(16);
            flow.setVgap(16);
            flow.setAlignment(Pos.CENTER);

            for (Supplier sup: entry.getValue()) {
                SupplierCard card = new SupplierCard();
                card.setData(sup);
                card.prefWidthProperty().bind(suppliersContainer.widthProperty());
                card.setOnEditAction(this::openEditSupplier);

                flow.getChildren().add(card);
            }

            suppliersContainer.getChildren().addAll(groupLabel, flow);
        }
    }

    private void openEditSupplier(Supplier sup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-supplier-form.fxml"));
            EditSupplierForm controller = new EditSupplierForm();
            loader.setController(controller);
            Parent formulario = loader.load();

            controller.setOnSaveSuccess(this::loadSuppliers);
            controller.setSupplierData(sup);
            Stage curStage = ((Stage) suppliersContainer.getScene().getWindow());
            ModalManager.openModal(curStage, formulario, "Editar Fornecedor: " + sup.getName(), 600, 400, "ModalFrameSM.fxml", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            renderSuppliersGroup(allSuppliers);
            return;
        }

        List<Supplier> filtered = allSuppliers.stream().filter(sup -> sup.getName().toLowerCase().contains(query) || sup.getCnpj().toLowerCase().contains(query) || sup.getPaymentMethod().toLowerCase().contains(query)).toList();

        renderSuppliersGroup(filtered);

        if (filtered.isEmpty()) {
            setErrorMessage("Nenhum fornecedor encontrado.");
        }
    }

    public void handleProductsPage() {
        NavigationManager nm = NavigationManager.getInstance();
        StackPane container = (StackPane) suppliersContainer.getScene().getRoot().lookup("#container");
        nm.navigateToPage(container, "view/products-page.fxml", "Fornecedores >> Produtos e Cadastro", "Onde você e os outros gerentes poderão ver e gerenciar os produtos de cada fornecedor.");
    }


    @FXML
    public void handleOpenAddDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-supplier-form.fxml"));
        AddSupplierForm controller = new AddSupplierForm();
        loader.setController(controller);
        Parent formulario = loader.load();

        controller.setOnSaveSuccess(this::loadSuppliers);
        Stage curStage = ((Stage) btnNewSupplier.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Cadastrar Fornecedor", 600, 400, "ModalFrameSM.fxml", true);
    }

    private void handleDelete(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja excluir este fornecedor?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    ApiClient.delete("/api/suppliers/" + id);
                    loadSuppliers();
                } catch (Exception e) {
                    statusLabel.setText("Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }
}