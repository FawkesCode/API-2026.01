package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.QuotationProposalItem;

import java.math.BigDecimal;

public record QuotationProposalItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
    public static QuotationProposalItemResponse fromEntity(QuotationProposalItem item) {
        return new QuotationProposalItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }
}
