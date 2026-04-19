package com.fawkes.api.DTOs;

import java.math.BigDecimal;

public record CheaperProductDTO(
        String productName,
        String productType,
        String nomeFornecedor,
        BigDecimal unitValue,
        Integer currentStockQuantity
) {}
