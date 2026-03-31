package com.fawkes.front;

import com.fawkes.front.utils.NavigationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException {
        NavigationManager nm = new NavigationManager();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 600);
        stage.setTitle("NEWE - We Logic");
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/logo-icon.png")));
        stage.getIcons().add(icon);
        stage.setResizable(false);

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
