package com.fawkes.front.components;

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

    @FXML private Label productType;
    @FXML private Label productName;
    @FXML private Label productSupplier;
    @FXML private Label productQtd;
    @FXML private Label productPrice;

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
        productType.setText(item.getProductType());
        productName.setText(item.getProductName());
        productSupplier.setText(item.getSupplierName());
        productQtd.setText(item.getCurrentStockQuantity().toString());


        if (item.isLow()) {
            this.getStyleClass().add("stock-item--low");
        } else {
            this.getStyleClass().remove("stock-item--low");
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