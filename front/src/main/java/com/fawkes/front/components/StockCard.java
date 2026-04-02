package com.fawkes.front.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class StockCard extends AnchorPane {

    public StockCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/components/StockCard.fxml"));

        String css = Objects.requireNonNull(getClass().getResource("/com/fawkes/front/styles/components/stock.css")).toExternalForm();
        this.getStylesheets().add(css);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
