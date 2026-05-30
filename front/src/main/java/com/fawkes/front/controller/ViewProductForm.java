package com.fawkes.front.controller;

import com.fawkes.front.models.StockItem;
import com.fawkes.front.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.util.Locale;

public class ViewProductForm {
    @FXML private Label nameLabel;
    @FXML private Label supplierLabel;
    @FXML private Label curQtdLabel;
    @FXML private Label minQtdLabel;
    @FXML private Label maxQtdLabel;
    @FXML private Label priceLabel;
    @FXML private Label measUnitLabel;
    @FXML private Label typeLabel;

    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public void setProductData(StockItem pro) {
        nameLabel.setText(pro.getProductName());
        supplierLabel.setText(pro.getSupplierName() != null && !pro.getSupplierName().isBlank()
                ? pro.getSupplierName() : "Sem Fornecedor");
        curQtdLabel.setText(pro.getCurrentStockQuantity() != null
                ? pro.getCurrentStockQuantity().toString() : "0");
        minQtdLabel.setText(pro.getMinStockQuantity() != null
                ? pro.getMinStockQuantity().toString() : "0");
        maxQtdLabel.setText(pro.getMaxStockQuantity() != null
                ? pro.getMaxStockQuantity().toString() : "0");
        priceLabel.setText(CURRENCY_FMT.format(pro.getUnitValue()));
        measUnitLabel.setText(StringUtils.measureTranslation(pro.getMeasurementUnit()));
        typeLabel.setText(pro.getProductType());
    }
}