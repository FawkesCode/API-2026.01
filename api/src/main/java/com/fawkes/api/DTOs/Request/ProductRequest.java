package com.fawkes.api.DTOs.Request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String productName;
    private String productType;
    private String measurementUnit;
    @NotNull
    private BigDecimal unitValue;
    private String description;
    @NotNull
    private Long supplierId;
    @NotNull
    private Long stockId;
}
