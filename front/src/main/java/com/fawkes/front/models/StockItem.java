package com.fawkes.front.models;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;

/*
 Espelha o StockItemDTO do back-end.
 Campos: productId, productName, productType, measurementUnit,
         unitValue, currentStockQuantity, minStockQuantity, maxStockQuantity.
 */
public class StockItem {

    private Long productId;
    private String productName;
    private String productType;
    private String measurementUnit;
    private BigDecimal unitValue;
    private Integer currentStockQuantity;
    private Integer minStockQuantity;
    private Integer maxStockQuantity;

    public StockItem() {}

    public StockItem(Long productId, String productName, String productType,
                     String measurementUnit, BigDecimal unitValue,
                     Integer currentStockQuantity, Integer minStockQuantity,
                     Integer maxStockQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.measurementUnit = measurementUnit;
        this.unitValue = unitValue;
        this.currentStockQuantity = currentStockQuantity;
        this.minStockQuantity = minStockQuantity;
        this.maxStockQuantity = maxStockQuantity;
    }

    public static StockItem fromJson(JsonNode node) {
        Long productId = node.path("productId").asLong();
        String productName = node.path("productName").asText("-");
        String productType = node.path("productType").asText("-");
        String measurementUnit = node.path("measurementUnit").asText("-");
        BigDecimal unitValue = new BigDecimal(node.path("unitValue").asText("0"));
        Integer current = node.path("currentStockQuantity").asInt(0);
        Integer min = node.path("minStockQuantity").asInt(0);
        Integer max = node.path("maxStockQuantity").asInt(0);
        return new StockItem(productId, productName, productType,
                measurementUnit, unitValue, current, min, max);
    }

    /** Retorna true se o estoque atual está em ou abaixo do mínimo */
    public boolean isLow() {
        return currentStockQuantity != null && minStockQuantity != null
                && currentStockQuantity <= minStockQuantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }

    public BigDecimal getUnitValue() { return unitValue; }
    public void setUnitValue(BigDecimal unitValue) { this.unitValue = unitValue; }

    public Integer getCurrentStockQuantity() { return currentStockQuantity; }
    public void setCurrentStockQuantity(Integer currentStockQuantity) { this.currentStockQuantity = currentStockQuantity; }

    public Integer getMinStockQuantity() { return minStockQuantity; }
    public void setMinStockQuantity(Integer minStockQuantity) { this.minStockQuantity = minStockQuantity; }

    public Integer getMaxStockQuantity() { return maxStockQuantity; }
    public void setMaxStockQuantity(Integer maxStockQuantity) { this.maxStockQuantity = maxStockQuantity; }
}