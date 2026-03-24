package com.fawkes.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/layout-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Fawkes - 2026.1");
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
