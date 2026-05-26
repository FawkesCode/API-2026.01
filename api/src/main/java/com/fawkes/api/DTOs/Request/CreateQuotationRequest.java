package com.fawkes.api.DTOs.Request;

import lombok.Data;

@Data
public class CreateQuotationRequest {
    private Long purchaseOrderId;
}
