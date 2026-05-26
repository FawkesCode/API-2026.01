package com.fawkes.api.DTOs;

import com.fawkes.api.Entities.Quotation;

import java.time.LocalDateTime;
import java.util.List;

public record QuotationResponse(
        Long id,
        String quotationNumber,
        String status,
        Long purchaseOrderId,
        List<QuotationProposalResponse> proposals,
        LocalDateTime createdAt
) {
    public static QuotationResponse fromEntity(Quotation q) {
        List<QuotationProposalResponse> proposals = q.getProposals() != null
                ? q.getProposals().stream().map(QuotationProposalResponse::fromEntity).toList()
                : List.of();
        return new QuotationResponse(
                q.getId(),
                q.getQuotationNumber(),
                q.getStatus().name(),
                q.getPurchaseOrder().getId(),
                proposals,
                q.getCreatedAt()
        );
    }
}
