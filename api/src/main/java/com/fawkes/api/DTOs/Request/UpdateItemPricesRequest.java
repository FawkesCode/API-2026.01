package com.fawkes.api.DTOs.Request;

import java.math.BigDecimal;
import java.util.List;

public record UpdateItemPricesRequest(
    List<ItemPriceEntry> items
) {
    public record ItemPriceEntry(Long itemId, BigDecimal unitPrice) {}
}
