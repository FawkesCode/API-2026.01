package com.fawkes.front;

import com.fawkes.front.utils.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;


public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        new NavigationManager().navigateToLogin(stage);

    }

    public static void main(String[] args) {
        launch();
    }
}
