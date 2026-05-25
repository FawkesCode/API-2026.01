package com.fawkes.api.DTOs.Request;

import java.time.LocalDate;
import java.util.List;

public record ReceiveOrderRequest(
    List<ReceivedItemRequest> items,
    String invoiceNumber,
    String invoiceSerie,
    LocalDate invoiceDate
) {
    public record ReceivedItemRequest(Long productId, Integer receivedQuantity) {}
}