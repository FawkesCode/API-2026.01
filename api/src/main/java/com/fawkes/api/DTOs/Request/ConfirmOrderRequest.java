package com.fawkes.api.DTOs.Request;

import java.time.LocalDateTime;

public record ConfirmOrderRequest(
        LocalDateTime expectedDeliveryDate,
        String reason
) {}
