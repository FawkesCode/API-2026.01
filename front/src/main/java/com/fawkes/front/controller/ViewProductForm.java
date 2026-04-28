package com.fawkes.front.controller;

import com.fawkes.front.models.Employee;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.utils.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Double.parseDouble;

public class ViewProductForm {
    @FXML private Label nameLabel;
    @FXML private Label supplierLabel;
    @FXML private Label curQtdLabel;
    @FXML private Label minQtdLabel;
    @FXML private Label maxQtdLabel;
    @FXML private Label priceLabel;
    @FXML private Label measUnitLabel;
    @FXML private Label typeLabel;
    @FXML private Label fiscalNumberLabel;

    private StockItem product;
    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


    public void setProductData(StockItem pro) {
        this.product = pro;
        nameLabel.setText(pro.getProductName());
        supplierLabel.setText("Sem fornecedores no endpoint");
        curQtdLabel.setText(pro.getCurrentStockQuantity().toString());
        minQtdLabel.setText(pro.getMinStockQuantity().toString());
        maxQtdLabel.setText(pro.getMaxStockQuantity().toString());
        //double price =  parseDouble(pro.getUnitValue().toString()) ;
        priceLabel.setText(CURRENCY_FMT.format(pro.getUnitValue()));
        measUnitLabel.setText(StringUtils.measureTranslation(pro.getMeasurementUnit()));
        typeLabel.setText(pro.getProductType());
    }


}
