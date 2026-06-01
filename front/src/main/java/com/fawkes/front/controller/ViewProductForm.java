package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fawkes.front.models.StockItem;
import com.fawkes.front.service.ApiClient;
import com.fawkes.front.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class ViewProductForm {
    @FXML private Label    nameLabel;
    @FXML private Label    supplierLabel;
    @FXML private Label    curQtdLabel;
    @FXML private TextField minQtdField;
    @FXML private TextField maxQtdField;
    @FXML private Label    priceLabel;
    @FXML private Label    measUnitLabel;
    @FXML private Label    typeLabel;
    @FXML private Label    errorLabel;

    private StockItem product;
    private Runnable onSaveSuccess;

    private static final NumberFormat CURRENCY_FMT =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public void setOnSaveSuccess(Runnable r) { this.onSaveSuccess = r; }

    public void setProductData(StockItem pro) {
        this.product = pro;
        nameLabel.setText(pro.getProductName());
        supplierLabel.setText(pro.getSupplierName() != null && !pro.getSupplierName().isBlank()
                ? pro.getSupplierName() : "Sem Fornecedor");
        curQtdLabel.setText(pro.getCurrentStockQuantity() != null
                ? pro.getCurrentStockQuantity().toString() : "0");
        minQtdField.setText(pro.getMinStockQuantity() != null
                ? pro.getMinStockQuantity().toString() : "0");
        maxQtdField.setText(pro.getMaxStockQuantity() != null
                ? pro.getMaxStockQuantity().toString() : "0");
        priceLabel.setText(CURRENCY_FMT.format(pro.getUnitValue()));
        measUnitLabel.setText(StringUtils.measureTranslation(pro.getMeasurementUnit()));
        typeLabel.setText(pro.getProductType());
    }

    @FXML
    private void handleSaveLimits() {
        try {
            int min = Integer.parseInt(minQtdField.getText().trim());
            int max = Integer.parseInt(maxQtdField.getText().trim());

            if (min < 0 || max < 0) {
                errorLabel.setText("Valores não podem ser negativos.");
                return;
            }
            if (max > 0 && min > max) {
                errorLabel.setText("Mínimo não pode ser maior que o máximo.");
                return;
            }

            String body = new ObjectMapper().writeValueAsString(
                    Map.of("minStockQuantity", min, "maxStockQuantity", max));

            ApiClient.patch("/api/product-stock/" + product.getProductId() + "/limits", body);

            if (onSaveSuccess != null) onSaveSuccess.run();
            ((Stage) minQtdField.getScene().getWindow()).close();

        } catch (NumberFormatException ex) {
            errorLabel.setText("Digite apenas números inteiros.");
        } catch (Exception ex) {
            errorLabel.setText("Erro: " + ex.getMessage());
        }
    }
}