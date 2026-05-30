package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.ProductStock;

import java.math.BigDecimal;

public record StockItemDTO(
        Long productId,
        String productName,
        String productType,
        String measurementUnit,
        BigDecimal unitValue,
        Integer currentStockQuantity,
        Integer minStockQuantity,
        Integer maxStockQuantity,
        Long stockId,
        String supplierName   // adicionado
) {
    public static StockItemDTO fromEntity(ProductStock ps) {
        String supplier = ps.getProduct().getSuppliers() != null
                ? ps.getProduct().getSuppliers().getSupplierName()
                : "Sem Fornecedor";

        return new StockItemDTO(
                ps.getProduct().getId(),
                ps.getProduct().getProductName(),
                ps.getProduct().getProductType(),
                ps.getProduct().getMeasurementUnit().name(),
                ps.getProduct().getUnitValue(),
                ps.getCurrentStockQuantity(),
                ps.getMinStockQuantity(),
                ps.getMaxStockQuantity(),
                ps.getProduct().getStock() != null ? ps.getProduct().getStock().getId() : null,
                supplier
        );
    }
}