package com.fawkes.front.controller;

import com.fawkes.front.service.UserInfoManager;
import com.fawkes.front.utils.NavigationManager;
import com.fawkes.front.utils.RBACUtil;
import com.fawkes.front.utils.StringUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.CENTER_RIGHT;

public class LayoutController {
    @FXML
    private AnchorPane sidebarContainer;
    @FXML
    private ImageView logoContainer;
    @FXML
    private Pane userPane;
    @FXML
    private StackPane contentWrapper;
    @FXML
    private Label pageName;
    @FXML
    private Label pageDescription;
    @FXML
    private ScrollPane contentScrollPane;
    @FXML
    private VBox mainContent;
    @FXML
    private ImageView btnIcon;
    @FXML
    private StackPane menuBtnContainer;
    @FXML
    private Separator sidebarSeparator;
    @FXML
    private Separator sidebarSeparator1;
    @FXML
    private Separator sidebarSeparator2;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnHistory;
    @FXML
    private Button btnEmployees;
    @FXML
    private Button btnSuppliers;
    @FXML
    private Button btnStock;
    @FXML
    private ImageView iconDashboard;
    @FXML
    private Label sidebarUserName;
    @FXML
    private Label sidebarUserRole;


    private boolean isSidebarOpen = true;
    private final Image iconMenu = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/menu-icon.png")));
    private final Image iconClose = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/close-icon.png")));

    private final Map<Button, String> navBtnLabels = new HashMap<>();

    NavigationManager nm = NavigationManager.getInstance();



    @FXML
    public void initialize() {
        // Logged User Configurations
        UserInfoManager loggedUser = UserInfoManager.getInstance();
        sidebarUserName.setText(StringUtils.roleTranslation(loggedUser.getUserName()));
        sidebarUserRole.setText(StringUtils.roleTranslation(loggedUser.getUserRole()));

        // Current Page Configurations
        pageName.textProperty().bind(nm.getCurrentPage());
        pageDescription.textProperty().bind(nm.getCurrentPageDescription());

        // Sidebar Configurations
        btnIcon.setImage(iconClose);
        mainContent.minHeightProperty().bind(contentScrollPane.heightProperty());
        sidebarSeparator.maxWidthProperty().bind(sidebarContainer.widthProperty());
        sidebarSeparator1.maxWidthProperty().bind(sidebarContainer.widthProperty());
        sidebarSeparator2.maxWidthProperty().bind(sidebarContainer.widthProperty());

        // Apply RBAC restrictions based on user role
        applyRBACRestrictions();
    }

    private void applyRBACRestrictions() {
        // OPERATIONAL users can only access Stock
        if (RBACUtil.isOperational()) {
            // Hide tabs for operational users
            btnDashboard.setVisible(false);
            btnDashboard.setManaged(false);

            btnHistory.setVisible(false);
            btnHistory.setManaged(false);

            btnEmployees.setVisible(false);
            btnEmployees.setManaged(false);

            btnSuppliers.setVisible(false);
            btnSuppliers.setManaged(false);

            // Navigate to Stock by default for operational users
            handleStockButton();
        } else if (RBACUtil.isManager()) {
            handleDashboardButton();
        } else {
            // Maybe it will be needed to take off some buttons from here as well, depending on what exactly appears for the director
            handleDashboardButton();
        }
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
            btnIcon.setImage(iconMenu);
            btnIcon.setFitHeight(15);
            btnIcon.setFitWidth(15);
            menuBtnContainer.setAlignment(CENTER);

            timeline.setOnFinished(event -> {
                for (Node node : navBtn) {
                    if (node instanceof Button btn) {
                        if (!navBtnLabels.containsKey(btn)) {
                            navBtnLabels.put(btn, btn.getText());
                        }
                        btn.setText(null);
                        btn.setAlignment(CENTER);
                        btn.getStyleClass().add("sidebar__button--closed");
                    }
                }
            });

        } else {
            logoContainer.setVisible(true);
            userPane.setVisible(true);
            btnIcon.setImage(iconClose);
            btnIcon.setFitHeight(8);
            btnIcon.setFitWidth(8);

            menuBtnContainer.setAlignment(CENTER_RIGHT);

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


    private void updateActiveButton(Button clickedButton) {
        var navButtons = sidebarContainer.lookupAll(".sidebar__button");

        for (Node node : navButtons) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("sidebar__button--active");

                if (btn.getGraphic() instanceof ImageView iv) {
                    String currentUrl = iv.getImage().getUrl();
                    String cleanUrl = currentUrl.replace("--active.png", ".png");

                    if (btn == clickedButton) {
                        iv.setImage(new Image(cleanUrl.replace(".png", "--active.png")));
                    } else {
                        iv.setImage(new Image(cleanUrl));
                    }
                }
            }
        }

        clickedButton.getStyleClass().add("sidebar__button--active");
    }

    public void handleDashboardButton() {
        nm.navigateToPage(contentWrapper, "view/dashboard-page.fxml", "Dashboard", "Onde você e outros gerentes podem visualizar a situação das compras de forma rápida e simplificada.");
        updateActiveButton(btnDashboard);
    }

    public void handleHistoryButton() {
        nm.navigateToPage(contentWrapper, "view/history-page.fxml", "Atividade Recente", "Aqui é onde você e outros gerentes podem acessar e visualizar todas as alterações dentro do sistema.");
        updateActiveButton(btnHistory);
    }

    public void handleEmployeesButton() {
        nm.navigateToPage(contentWrapper, "view/employees-page.fxml", "Funcionários", "Aqui é onde você e outros gerentes podem administrar os funcionários cadastrados na plataforma.");
        updateActiveButton(btnEmployees);
    }

    public void handleSuppliersButton() {
        nm.navigateToPage(contentWrapper, "view/suppliers-page.fxml", "Fornecedores", "Aqui é onde você e outros gerentes podem administrar os fornecedores cadastrados na plataforma.");
        updateActiveButton(btnSuppliers);
    }

    public void handleStockButton() {
        nm.navigateToPage(contentWrapper, "view/stock-page.fxml", "Estoque", "Onde você e os outros gerentes poderão ver e gerenciar os produtos estocados pela empresa.");
        updateActiveButton(btnStock);
    }

    public void handleLogout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        nm.navigateToLogin(stage);
    }




}