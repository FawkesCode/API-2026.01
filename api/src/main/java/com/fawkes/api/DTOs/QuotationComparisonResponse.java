package com.fawkes.api.DTOs;

import java.math.BigDecimal;
import java.util.List;

public record QuotationComparisonResponse(
        Long quotationId,
        String quotationNumber,
        List<QuotationProposalResponse> proposals,
        Long lowestTotalProposalId,
        BigDecimal lowestTotalValue,
        Long shortestDeliveryProposalId,
        Integer shortestDeliveryDays
) {
    public static QuotationComparisonResponse fromProposals(
            Long quotationId,
            String quotationNumber,
            List<QuotationProposalResponse> proposals
    ) {
        Long lowestTotalId = null;
        BigDecimal lowestTotal = null;
        Long shortestDeliveryId = null;
        Integer shortestDays = null;

        for (QuotationProposalResponse p : proposals) {
            if (lowestTotal == null || p.totalValue().compareTo(lowestTotal) < 0) {
                lowestTotal = p.totalValue();
                lowestTotalId = p.id();
            }
            if (shortestDays == null || p.deliveryDays() < shortestDays) {
                shortestDays = p.deliveryDays();
                shortestDeliveryId = p.id();
            }
        }

        return new QuotationComparisonResponse(
                quotationId,
                quotationNumber,
                proposals,
                lowestTotalId,
                lowestTotal,
                shortestDeliveryId,
                shortestDays
        );
    }
}
