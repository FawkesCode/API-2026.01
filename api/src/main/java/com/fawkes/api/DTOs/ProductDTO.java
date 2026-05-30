package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.Products;

import java.math.BigDecimal;

public record ProductDTO(
        Long productId,
        String productName,
        String productType,
        String measurementUnit,
        BigDecimal unitValue,
        String description,
        Long supplierId,
        String supplierName,
        Long stockId,
        String stockName
) {
    public static ProductDTO fromEntity(Products p) {
        return new ProductDTO(
                p.getId(),
                p.getProductName(),
                p.getProductType(),
                p.getMeasurementUnit() != null ? p.getMeasurementUnit().name() : "NAO_DEFINIDO",
                p.getUnitValue(),
                p.getDescription(),
                p.getSuppliers() != null ? p.getSuppliers().getId() : null,
                p.getSuppliers() != null ? p.getSuppliers().getSupplierName() : "Sem Fornecedor",
                p.getStock() != null ? p.getStock().getId() : null,
                p.getStock() != null ? p.getStock().getStockName() : null
        );
    }
}