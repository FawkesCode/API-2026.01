package com.fawkes.api.DTOs.Request;

import java.time.LocalDateTime;

public record UpdateOrderRequest(
    String notes,
    LocalDateTime expectedDeliveryDate
) {}
