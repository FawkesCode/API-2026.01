package com.fawkes.front.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ModalFrameController {
    @FXML private Label modalTitle;
    @FXML private StackPane modalContent;
    @FXML private Label instructions;

    public void setModalTitle(String title) {
        modalTitle.setText(title);
    }

    public void setContent(Node node) {
        modalContent.getChildren().setAll(node);
    }

    public void visibleInstructions() {
        instructions.setVisible(true);
    }

    public void invisibleInstructions() {
        instructions.setVisible(false);
    }

    @FXML
    private void closeModal(ActionEvent event) {
        ((Stage) modalTitle.getScene().getWindow()).close();
    }


}
