package com.fawkes.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/layout-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("NEWE - We Logic");
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/logo-icon.png")));
        stage.getIcons().add(icon);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
