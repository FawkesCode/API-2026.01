package com.fawkes.front.components;

import com.fawkes.front.models.StockItem;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class ProductShopCard extends AnchorPane {
    @FXML private JFXButton btnDelete;
    @FXML private JFXButton btnEdit;
    @FXML private Label productName;
    @FXML private Label productPrice;
    @FXML private Label productType;
    @FXML private Label productSupplier;


    private StockItem product;
    private Consumer<StockItem> onAddToCart;

    public void setOnAddToCart(Consumer<StockItem> action) {
        this.onAddToCart = action;
    }

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public ProductShopCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/ProductShopCard.fxml"));
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
        productPrice.setText(CURRENCY.format(item.getUnitValue()));
        productSupplier.setText(item.getSupplierName());

        this.product = item;
    }

    @FXML
    public void addToCart(){
        if (onAddToCart != null) {
            onAddToCart.accept(this.product);
        }
    }
}
