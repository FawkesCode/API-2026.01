package com.fawkes.front.controller;

import com.fawkes.front.models.*;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.service.UserInfoManager;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.StringUtils;
import com.fawkes.front.controller.QuoteRequestForm;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PendingRequestForm {

    @FXML private JFXButton btnApprove;
    @FXML private JFXButton btnDecline;
    @FXML private Label     costCenter;
    @FXML private Label     department;
    @FXML private Label     description;
    @FXML private Label     paymentMethod;
    @FXML private VBox      productsContainer;
    @FXML private Label     requisitor;
    @FXML private VBox      suppliersContainer;
    @FXML private Label     totalPrice;
    @FXML private Label     totalQuantity;
    @FXML private HBox      btnActionContainer;
    @FXML private VBox      invoiceContainer;
    @FXML private Label     invoiceNumber;

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private Stage    curStage;
    private Order    order;
    private Runnable onSaveSuccess;

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    private final UserInfoManager loggedUser = UserInfoManager.getInstance();

    public void initialize() {}

    public void setData(Order order, Stage curStage) {
        this.curStage = curStage;
        this.order    = order;

        department.setText(order.getSector());
        description.setText(order.getDescription());
        if (order.getDecisionReason() != null && !order.getDecisionReason().isBlank()) {
            description.setText(order.getDescription() + "\n\nJustificativa: " + order.getDecisionReason());
        }
        paymentMethod.setText(StringUtils.paymentTranslation(order.getPaymentMethod()));
        requisitor.setText(order.getRequesterName());
        totalPrice.setText("Total: " + CURRENCY.format(order.getTotalValue()));
        totalQuantity.setText("Qtd. de Itens: " + order.getQuantity());

        renderProducts(order.getItemsList());
        renderSuppliers(order.getSuppliersList());
        renderActions(order.getStatus());

        // Exibe NF se o pedido foi recebido
        if ("received".equals(order.getStatus()) && order.getInvoiceNumber() != null) {
            invoiceNumber.setText("Nº " + order.getInvoiceNumber());
            invoiceContainer.setVisible(true);
            invoiceContainer.setManaged(true);
        }
    }


    private void renderActions(String status) {
        btnActionContainer.getChildren().clear();

        String  role              = loggedUser.getUserRole();
        boolean isDirectorManager = "DIRECTOR".equals(role) || "MANAGER".equals(role);
        boolean canReceive        = isDirectorManager || "OPERATIONAL".equals(role);

        switch (status != null ? status : "") {
            case "pending" -> {
                if (isDirectorManager) {
                    btnActionContainer.getChildren().add(
                            makeBtn("📋  Registrar Cotação", "btn--info", this::handleQuote)
                    );
                } else {
                    btnActionContainer.getChildren().add(infoLabel("⏳  Sob revisão — aguardando cotação"));
                }
            }
            case "quoted" -> {
                if ("DIRECTOR".equals(role)) {
                    btnActionContainer.getChildren().addAll(
                            makeBtn("✓  Marcar como Comprado", "btn--submit", this::handleAproved),
                            makeBtn("✗  Negar Pedido", "btn--danger", this::handleDeclined)
                    );
                } else {
                    btnActionContainer.getChildren().add(infoLabel("📋  Cotação registrada — aguardando decisão do diretor"));
                }
            }
            case "confirmed" -> {
                if (isDirectorManager) {
                    btnActionContainer.getChildren().add(
                            makeBtn("🚚  Marcar como Enviado", "btn--info", this::handleShip)
                    );
                } else {
                    btnActionContainer.getChildren().add(infoLabel("✓  Aprovado — aguardando envio"));
                }
            }
            case "shipped" -> {
                // verifica se está em atraso
                String effective = order.getEffectiveStatus();
                if ("overdue".equals(effective)) {
                    btnActionContainer.getChildren().add(infoLabel("⚠  Entrega em atraso"));
                }
                if (canReceive) {
                    btnActionContainer.getChildren().add(
                            makeBtn("📦  Confirmar Recebimento", "btn--submit", this::handleReceive)
                    );
                }
            }
            case "received" -> {
                btnActionContainer.getChildren().add(infoLabel("✅  Pedido finalizado e recebido"));
                if (isDirectorManager) {
                    btnActionContainer.getChildren().add(
                            makeBtn("⚠  Reportar Problema", "btn--danger", this::handleProblem)
                    );
                }
            }
            case "problem" -> {
                btnActionContainer.getChildren().add(infoLabel("⚠  Problema no recebimento reportado"));
                if (isDirectorManager) {
                    btnActionContainer.getChildren().add(
                            makeBtn("↩  Confirmar Devolução", "btn--danger", this::handleReturn)
                    );
                }
            }
            case "returned" ->
                    btnActionContainer.getChildren().add(infoLabel("↩  Pedido devolvido ao fornecedor"));

            case "cancelled" ->
                    btnActionContainer.getChildren().add(infoLabel("❌  Pedido cancelado / recusado"));
            case "draft" ->
                    btnActionContainer.getChildren().add(infoLabel("📝  Rascunho — ainda não enviado"));
            default ->
                    btnActionContainer.getChildren().add(infoLabel("Status: " + status));
        }
    }


    @FXML
    public void handleAproved() {
        abrirSubModal(new AproveRequestForm(), "Aprovando Pedido " + order.getId());
    }

    public void handleDeclined() {
        abrirSubModal(new DeclineRequestForm(), "Recusando Pedido " + order.getId());
    }

    private void handleShip() {
        abrirSubModal(new ShipRequestForm(), "Confirmando Envio — Pedido " + order.getId());
    }

    private void handleReceive() {
        abrirSubModal(new ReceiveRequestForm(), "Recebendo Pedido " + order.getId());
    }
    private void handleProblem() {
        abrirSubModal(new ProblemRequestForm(), "Reportando Problema — Pedido " + order.getId());
    }

    private void handleQuote() {
        abrirSubModal(new QuoteRequestForm(), "Registrar Cotação — Pedido " + order.getId());
    }
    private void handleReturn() {
        try {
            ApiClient.post("/api/purchase-orders/" + order.getId() + "/return", "{}");
            if (onSaveSuccess != null) onSaveSuccess.run();
            ((Stage) btnActionContainer.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseModal() {
        ((Stage) btnActionContainer.getScene().getWindow()).close();
    }

    private void abrirSubModal(Object controller, String titulo) {
        try {
            String fxmlPath = (controller instanceof QuoteRequestForm)
                    ? "/com/fawkes/front/view/forms/quote-request-form.fxml"
                    : "/com/fawkes/front/view/forms/director-request-form.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            Parent formulario = loader.load();

            if (controller instanceof AproveRequestForm a) {
                a.setData(order); a.setOnSaveSuccess(onSaveSuccess);
            } else if (controller instanceof DeclineRequestForm d) {
                d.setData(order); d.setOnSaveSuccess(onSaveSuccess);
            } else if (controller instanceof ShipRequestForm s) {
                s.setData(order); s.setOnSaveSuccess(onSaveSuccess);
            } else if (controller instanceof ReceiveRequestForm r) {
                r.setData(order); r.setOnSaveSuccess(onSaveSuccess);
            } else if (controller instanceof ProblemRequestForm p) {
                p.setData(order); p.setOnSaveSuccess(onSaveSuccess);
            } else if (controller instanceof QuoteRequestForm q) {
                q.setData(order); q.setOnSaveSuccess(onSaveSuccess);
            }

            Stage stageAtual = (Stage) btnActionContainer.getScene().getWindow();
            double height = (controller instanceof QuoteRequestForm) ? 420.0 : 350.0;
            Platform.runLater(() -> {
                stageAtual.close();
                ModalManager.openModal(curStage, formulario, titulo,
                        700.0, height, "ModalFrameM_heightSM.fxml", false);
            });

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("CAUSA: " + e.getCause());
        }
    }

    private JFXButton makeBtn(String text, String style, Runnable action) {
        JFXButton btn = new JFXButton(text);
        btn.getStyleClass().add(style);
        btn.setPrefHeight(26);
        btn.setPrefWidth(170);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Label infoLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("input__label--info");
        return lbl;
    }

    private void renderProducts(List<RequestItem> items) {
        productsContainer.getChildren().clear();
        for (RequestItem pro : items) {
            FormProducts fp = new FormProducts(
                    pro.getProduct().getName(), pro.getUnitPrice(),
                    pro.getQuantity(), pro.getProduct().getId(),
                    pro.getProduct().getSupplierId());

            Label qtd   = new Label("(x " + fp.getQuantity() + ")");
            Label name  = new Label(fp.getName());
            Label price = new Label(fp.getUnityPrice());
            qtd.getStyleClass().add("input__label--info");
            name.getStyleClass().add("input__label--info");
            price.getStyleClass().add("input__label--info");

            StackPane spacer = new StackPane();
            spacer.setStyle("-fx-border-style: dotted; -fx-border-color: #818EA1; -fx-border-width: 0 0 3 0;");
            spacer.setMinHeight(5);
            spacer.setMaxHeight(5);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(5, qtd, name, spacer, price);
            row.setAlignment(Pos.CENTER);
            productsContainer.getChildren().add(row);
        }
    }

    private void renderSuppliers(List<RequestSupplier> suppliers) {
        suppliersContainer.getChildren().clear();
        for (RequestSupplier sup : suppliers) {
            Label l = new Label(sup.getSupplierName());
            l.getStyleClass().add("input__label--info");
            suppliersContainer.getChildren().add(l);
        }
    }
}