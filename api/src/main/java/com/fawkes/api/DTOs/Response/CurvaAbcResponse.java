package com.fawkes.api.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurvaAbcResponse {

    private List<AbcItem> items;
    private long totalA;
    private long totalB;
    private long totalC;
    private double pctValueA;
    private double pctValueB;
    private double pctValueC;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AbcItem {
        private int rank;
        private Long productId;
        private String productName;
        private String productType;
        private String supplierName;
        private BigDecimal unitValue;
        private int totalQuantityOrdered;
        private BigDecimal totalValueOrdered;
        private double percentageOfTotal;
        private double cumulativePercentage;
        private String curva;   // "A" | "B" | "C"
    }
}
