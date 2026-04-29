package com.fawkes.front.components;

import com.fawkes.front.models.StockItem;
import com.fawkes.front.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
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

public class ProductSupplierCard extends AnchorPane {
    @FXML private JFXButton btnDelete;
    @FXML private JFXButton btnEdit;
    @FXML private Label productName;
    @FXML private Label productPrice;
    @FXML private Label productType;
    @FXML private Label productUnit;


    private StockItem product;
    private Consumer<StockItem> onEditAction;
    private Consumer<StockItem> onDeleteAction;

    public void setOnEditAction(Consumer<StockItem> action) {
        this.onEditAction = action;
    }
    public void setOnDeleteAction(Consumer<StockItem> action) { this.onDeleteAction = action; }


    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public ProductSupplierCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/ProductSupplierCard.fxml"));
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
        productUnit.setText(StringUtils.measureTranslation(item.getMeasurementUnit()));

        this.product = item;
    }


    public void openEditModal(){
        if (onEditAction != null) {
            onEditAction.accept(this.product);
        }
    }

    public void handleDeleteProduct() {
        if (onDeleteAction != null) {
            onDeleteAction.accept(this.product);
        }
    }
}
