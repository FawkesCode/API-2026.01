package com.fawkes.api.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalProductResponse {

    private List<CriticalItem> lowStock;
    private List<CriticalItem> outOfStock;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriticalItem {
        private Long productId;
        private String productName;
        private String productType;
        private String supplierName;
        private String stockName;
        private Integer availableQuantity;
        private Integer minStockQuantity;
        private Integer maxStockQuantity;
        private String severity;   // "OUT_OF_STOCK" | "CRITICAL" | "LOW"
    }
}
