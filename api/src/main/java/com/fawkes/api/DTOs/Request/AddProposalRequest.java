package com.fawkes.api.DTOs.Request;

import lombok.Data;

import java.util.List;

@Data
public class AddProposalRequest {
    private Long supplierId;
    private Integer deliveryDays;
    private String paymentConditions;
    private List<ProposalItemRequest> items;
}
