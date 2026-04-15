package com.fawkes.front.components;

import com.fawkes.front.models.StockItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class StockCard extends AnchorPane {

    // O StockCard.fxml não tem nenhum fx:id declarado.
    // Os labels são acessados via lookup por styleClass:
    //   .stock-item__name    → nome do produto
    //   .stock-item__number  → quantidade atual
    // O card foi projetado de forma simples (só nome + qtd visível).

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public StockCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/StockCard.fxml"));
        //String css = Objects.requireNonNull(
                        //etClass().getResource("/com/fawkes/front/styles/components/stock.scss"))
                //.toExternalForm();
        //this.getStylesheets().add(css);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(StockItem item) {
        Label nameLabel = (Label) this.lookup(".stock-item__name");
        if (nameLabel != null) {
            nameLabel.setText(item.getProductName().toUpperCase());
        }

        Label numberLabel = (Label) this.lookup(".stock-item__number");
        if (numberLabel != null) {
            numberLabel.setText(String.valueOf(item.getCurrentStockQuantity()));
        }

        // Destaca borda vermelha quando estoque está no mínimo ou abaixo
        if (item.isLow()) {
            this.setStyle("-fx-border-color: #FF4A50; -fx-border-width: 2px; -fx-border-radius: 8px;");
        } else {
            this.setStyle("");
        }
    }
}