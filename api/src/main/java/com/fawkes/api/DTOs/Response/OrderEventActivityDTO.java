package com.fawkes.api.DTOs.Response;

import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Entities.PurchaseOrderEvent;
import com.fawkes.api.Entities.PurchaseOrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderEventActivityDTO(
        Long eventId,
        Long orderId,
        String orderLabel,
        PurchaseOrder.Status fromStatus,
        PurchaseOrder.Status toStatus,
        String performedBy,
        String reason,
        LocalDateTime occurredAt
) {
    public static OrderEventActivityDTO from(PurchaseOrderEvent e) {
        PurchaseOrder order = e.getPurchaseOrder();
        Long orderId = order != null ? order.getId() : null;
        String label = "Pedido #" + (orderId != null ? orderId : "?");
        if (order != null) {
            List<PurchaseOrderItem> items = order.getItems();
            if (items != null && !items.isEmpty() && items.get(0).getProduct() != null) {
                String prod = items.get(0).getProduct().getProductName();
                label = "Pedido #" + orderId + " — " + prod;
                if (items.size() > 1) label += " (+" + (items.size() - 1) + ")";
            }
        }
        return new OrderEventActivityDTO(
                e.getId(), orderId, label,
                e.getFromStatus(), e.getToStatus(),
                e.getPerformedBy(), e.getReason(), e.getOccurredAt());
    }
}
