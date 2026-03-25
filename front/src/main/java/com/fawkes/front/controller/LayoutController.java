package com.fawkes.front.controller;

import com.fawkes.front.utils.NavigationManager;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class LayoutController {
    @FXML private AnchorPane sidebarContainer;
    @FXML private ImageView logoContainer;
    @FXML private Pane userPane;
    @FXML private StackPane contentWrapper;
    @FXML private Label pageName;
    @FXML private Label pageDescription;
    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox mainContent;

    private boolean isSidebarOpen = true;

    private final Map<Button, String> navBtnLabels = new HashMap<>();

    @FXML
    public void initialize() {
        NavigationManager.PageViewer.setCurPage("Dashboard", "Descrição do Dashboard");
        String name = NavigationManager.PageViewer.getCurPage();
        String description = NavigationManager.PageViewer.getCurDescr();

        pageName.setText(name);
        pageDescription.setText(description);

        mainContent.minHeightProperty().bind(contentScrollPane.heightProperty());
    }

    public void setPageInfo(String name, String description) {
        NavigationManager.PageViewer.setCurPage(name, description);
        pageName.setText(name);
        pageDescription.setText(description);
    }

    @FXML
    private void handleToggleSidebar() {
        var navBtn = sidebarContainer.lookupAll(".sidebar__button");

        double targetWidth = isSidebarOpen ? 85 : 250;
        Duration duration = Duration.millis(100);

        Timeline timeline = new Timeline(
                new KeyFrame(duration,
                        new KeyValue(sidebarContainer.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebarContainer.minWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebarContainer.maxWidthProperty(), targetWidth, Interpolator.EASE_BOTH)
                )
        );

        if (isSidebarOpen) {
            logoContainer.setVisible(false);
            userPane.setVisible(false);

            timeline.setOnFinished(event -> {
                for (Node node : navBtn) {
                    if (node instanceof Button btn) {
                        if (!navBtnLabels.containsKey(btn)) {
                            navBtnLabels.put(btn, btn.getText());
                        }
                        btn.setText(null);
                        btn.setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            });

        } else {
            logoContainer.setVisible(true);
            userPane.setVisible(true);

            for (Node node : navBtn) {
                if (node instanceof Button btn) {
                    btn.setText(navBtnLabels.get(btn));
                    btn.setAlignment(Pos.BASELINE_LEFT);
                }
            }
        }
        timeline.play();
        isSidebarOpen = !isSidebarOpen;
    }

    public void handleDashboardButton() {
        NavigationManager.navigateToPage(contentWrapper, "view/dashboard-page.fxml");
        setPageInfo("Dashboard", "Descrição do Dashboard");
    }

    public void handleHistoryButton() {
        NavigationManager.navigateToPage(contentWrapper, "view/history-page.fxml");
        setPageInfo("Atividade Recente", "Descrição das Atividades Recentes");
    }

    public void handleEmployeesButton() {
        NavigationManager.navigateToPage(contentWrapper, "view/employees-page.fxml");
        setPageInfo("Funcionários", "Descrição da tela de funcionários");
    }

    public void handleSuppliersButton() {
        NavigationManager.navigateToPage(contentWrapper, "view/suppliers-page.fxml");
        setPageInfo("Fornecedores", "Descrição da tela de forncedores");
    }

    public void handleStockButton() {
        NavigationManager.navigateToPage(contentWrapper, "view/stock-page.fxml");
        setPageInfo("Estoque", "Descrição da tela de Estoque");
    }






}