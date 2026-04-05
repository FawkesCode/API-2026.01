package com.fawkes.front.utils;

import com.fawkes.front.MainApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NavigationManager {

    private final StringProperty currentPage = new SimpleStringProperty("Nenhuma");
    private final StringProperty currentPageDescription = new SimpleStringProperty("");

    private void applyCommonResources(Stage stage, Scene scene) {
        stage.setTitle("NEWE - We Logic");
        scene.getStylesheets().add(getClass().getResource("/com/fawkes/front/styles/style.css").toExternalForm());

        if (stage.getIcons().isEmpty()) {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/logo-icon.png")));
            stage.getIcons().add(icon);
        }
        stage.setScene(scene);

    }

    public void navigateToLogin(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/fawkes/front/view/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 550, 600);

            applyCommonResources(stage, scene);

            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    public void navigateToApp(Stage curStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/layout-page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

            applyCommonResources(curStage, scene);

            curStage.setResizable(true);
            curStage.centerOnScreen();
            curStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void navigateToPage(Pane container, String pageUrl, String pageTitle, String pageDescr) {
        try {
            FXMLLoader loader =  new FXMLLoader(Objects.requireNonNull(MainApplication.class.getResource(pageUrl)));
            Parent root = loader.load();

            container.getChildren().clear();
            container.getChildren().add(root);

            currentPage.set(pageTitle);
            currentPageDescription.set(pageDescr);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StringProperty getCurrentPage() {
        return currentPage;
    }

    public StringProperty getCurrentPageDescription() {
        return currentPageDescription;
    }

}
