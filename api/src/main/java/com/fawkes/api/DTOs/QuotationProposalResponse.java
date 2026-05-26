package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.QuotationProposal;

import java.math.BigDecimal;
import java.util.List;

public record QuotationProposalResponse(
        Long id,
        Long supplierId,
        String supplierName,
        Integer deliveryDays,
        String paymentConditions,
        BigDecimal totalValue,
        String status,
        String justification,
        List<QuotationProposalItemResponse> items
) {
    public static QuotationProposalResponse fromEntity(QuotationProposal p) {
        List<QuotationProposalItemResponse> items = p.getItems() != null
                ? p.getItems().stream().map(QuotationProposalItemResponse::fromEntity).toList()
                : List.of();
        return new QuotationProposalResponse(
                p.getId(),
                p.getSupplier().getId(),
                p.getSupplier().getSupplierName(),
                p.getDeliveryDays(),
                p.getPaymentConditions(),
                p.getTotalValue(),
                p.getStatus().name(),
                p.getJustification(),
                items
        );
    }
}
