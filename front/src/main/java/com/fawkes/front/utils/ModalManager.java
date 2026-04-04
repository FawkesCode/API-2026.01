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

public class ModalManager {

    public static void openModal(Stage curStage, Parent specificContent, String title) {
        Stage overlayStage = new Stage();

        overlayStage.initModality(Modality.APPLICATION_MODAL);
        overlayStage.initStyle(StageStyle.TRANSPARENT);

        try {
            FXMLLoader loader = new FXMLLoader(ModalManager.class.getResource("/com/fawkes/front/view/components/ModalFrame.fxml"));
            Parent modalFrame = loader.load();
            ModalFrameController controller = loader.getController();
            controller.setContent(specificContent);
            controller.setModalTitle(title);

            StackPane backdrop = new StackPane();
            backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
            backdrop.getChildren().add(modalFrame);


            Scene scene = new Scene(backdrop);
            scene.setFill(Color.TRANSPARENT);


            backdrop.prefWidthProperty().bind(curStage.widthProperty());
            backdrop.prefHeightProperty().bind(curStage.heightProperty());

            scene.getStylesheets().add(Objects.requireNonNull(ModalManager.class.getResource("/com/fawkes/front/styles/components/modal.css")).toExternalForm());

            overlayStage.setScene(scene);
            overlayStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
