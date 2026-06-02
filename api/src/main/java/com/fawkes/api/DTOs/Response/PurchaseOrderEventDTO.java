package com.fawkes.api.DTOs.Response;

import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Entities.PurchaseOrderEvent;

import java.time.LocalDateTime;

public record PurchaseOrderEventDTO(
        Long id,
        PurchaseOrder.Status fromStatus,
        PurchaseOrder.Status toStatus,
        String performedBy,
        String reason,
        LocalDateTime occurredAt
) {
    public static PurchaseOrderEventDTO from(PurchaseOrderEvent e) {
        return new PurchaseOrderEventDTO(
                e.getId(),
                e.getFromStatus(),
                e.getToStatus(),
                e.getPerformedBy(),
                e.getReason(),
                e.getOccurredAt()
        );
    }
}
