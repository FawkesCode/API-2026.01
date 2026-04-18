package com.fawkes.front.utils;

import com.fawkes.front.components.ModalFrameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

import static com.fawkes.front.MainApplication.GLOBAL_CSS;

public class ModalManager {

    public static void openModal(Stage curStage, Parent specificContent, String title) {
        openModal(curStage, specificContent, title, 800.0, 400.0, "ModalFrame.fxml", true);
    }

    public static void openModal(Stage curStage, Parent specificContent, String title, Boolean hasIntructions) {
        openModal(curStage, specificContent, title, 800.0, 400.0, "ModalFrame.fxml", hasIntructions);
    }

    public static void openModal(Stage curStage, Parent specificContent, String title, double width, double height, String modalFXML, boolean hasInstructions) {
        Stage overlayStage = new Stage();

        overlayStage.initModality(Modality.APPLICATION_MODAL);
        overlayStage.initStyle(StageStyle.TRANSPARENT);
        String path = "/com/fawkes/front/view/components/" + modalFXML;

        try {

            FXMLLoader loader = new FXMLLoader(ModalManager.class.getResource(path));
            Parent modalFrame = loader.load();
            ModalFrameController controller = loader.getController();
            controller.setContent(specificContent);
            controller.setModalTitle(title);

            if (hasInstructions) {
                controller.visibleInstructions();
            } else {
                controller.invisibleInstructions();
            }

            StackPane backdrop = new StackPane();
            backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
            backdrop.getChildren().add(modalFrame);


            Scene scene = new Scene(backdrop);
            scene.setFill(Color.TRANSPARENT);


            backdrop.prefWidthProperty().bind(curStage.widthProperty());
            backdrop.prefHeightProperty().bind(curStage.heightProperty());

            scene.getStylesheets().add(GLOBAL_CSS);

            overlayStage.setScene(scene);
            overlayStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
