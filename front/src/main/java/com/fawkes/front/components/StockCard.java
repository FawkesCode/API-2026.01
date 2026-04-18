package com.fawkes.front.components;

import com.fawkes.front.models.Employee;
import com.fawkes.front.models.StockItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class StockCard extends AnchorPane {

    @FXML private ImageView productView;

    private StockItem product;
    private Consumer<StockItem> onEditAction;

    public void setOnEditAction(Consumer<StockItem> action) {
        this.onEditAction = action;
    }

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public StockCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/StockCard.fxml"));
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
            this.getStyleClass().add("stock-item--low");
        } else {
            this.getStyleClass().remove("stock-item--low");
        }

        if (item.getPicture() != null) {
            this.productView.setImage(item.getPicture());
        } else {
            try {
                Image placeholder = new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream(
                                        "/com/fawkes/front/img/placeholder-product.png")));
                this.productView.setImage(placeholder);
            } catch (Exception ignored) {
                // sem placeholder, deixa vazio
            }
        }
        this.product = item;
    }

    @FXML
    public void openViewModal(){
        if (onEditAction != null) {
            onEditAction.accept(this.product);
        }
    }
}