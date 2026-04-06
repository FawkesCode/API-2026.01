package com.fawkes.api.DTOs.Request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRequest {
    private String productName;
    private String productType;
    private String measurementUnit;
    private BigDecimal unitValue;
    private String description;
    private Long supplierId;
    private Long stockId;
}
