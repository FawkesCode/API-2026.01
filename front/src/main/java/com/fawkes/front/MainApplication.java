package com.fawkes.front;

import com.fawkes.front.utils.NavigationManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


public class MainApplication extends Application {
    public static String GLOBAL_CSS;

    @Override
    public void init() {
        Font.loadFont(getClass().getResourceAsStream("/com/fawkes/front/fonts/Montserrat-Bold.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/com/fawkes/front/fonts/Poppins-Regular.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/com/fawkes/front/fonts/SofiaSansCondensed-Regular.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/com/fawkes/front/fonts/SofiaSansCondensed-Bold.ttf"), 10);

        GLOBAL_CSS = Objects.requireNonNull(getClass().getResource("styles/bundle.css")).toExternalForm();
        Region warm_upRegion = new Region();
        Scene warm_upScene = new Scene(warm_upRegion);
        warm_upScene.getStylesheets().add(GLOBAL_CSS);
    }


    @Override
    public void start(Stage stage) throws IOException {
        new NavigationManager().navigateToLogin(stage);

    }

    public static void main(String[] args) {
        launch();
    }
}
