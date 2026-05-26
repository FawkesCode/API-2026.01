package com.fawkes.api.DTOs.Request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProposalItemRequest {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
