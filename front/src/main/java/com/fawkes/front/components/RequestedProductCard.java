package com.fawkes.front.components;

import com.fawkes.front.models.FormProducts;
import com.fawkes.front.models.StockItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class RequestedProductCard extends AnchorPane {
    @FXML private Label productPrice;
    @FXML private Label productName;
    @FXML private TextField quantityField;

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private FormProducts product;
    private Consumer<FormProducts> onAddItem;
    private Consumer<FormProducts> onRemoveItem;
    private Consumer<FormProducts> onDeleteItem;


    public void setOnAddItem(Consumer<FormProducts> action) {
        this.onAddItem = action;
    }
    public void setOnRemoveItem(Consumer<FormProducts> action) { this.onRemoveItem = action; }
    public void setOnDeleteItem(Consumer<FormProducts> action) { this.onDeleteItem = action; }


    public RequestedProductCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/RequestedProductCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FormProducts getProduct() {
        return this.product;
    }

    public void setData(StockItem pro) {
        productPrice.setText(CURRENCY.format(pro.getUnitValue()));
        productName.setText(pro.getProductName());


        this.product = new FormProducts(pro.getProductName(), Double.parseDouble(pro.getUnitValue().toString()), Integer.parseInt(quantityField.getText()), Integer.parseInt(pro.getProductId().toString()), pro.getSupplierID());
        quantityField.setText(String.valueOf(1));
    }

    @FXML
    private void handleAddQuantity() {
        if (onAddItem != null) {
            onAddItem.accept(this.product);
            quantityField.setText(String.valueOf(product.getQuantity()));
        }
    }

    @FXML
    private void handleRemoveQuantity() {
        if (onRemoveItem != null) {
            onRemoveItem.accept(this.product);
            quantityField.setText(String.valueOf(product.getQuantity()));
        }
    }

    @FXML
    private void handleDelete() {
        if (onDeleteItem != null) {
            onDeleteItem.accept(this.product);
        }
    }

}
