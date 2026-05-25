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
public class TopSupplierResponse {

    private List<SupplierRankItem> byVolume;
    private List<SupplierRankItem> byOrderCount;
    private List<SupplierRankItem> problematic;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierRankItem {
        private Long supplierId;
        private String supplierName;
        private long totalOrders;
        private BigDecimal totalValue;
        private long cancelledOrders;
        private double cancelRatePct;
        private Boolean isActive;
    }
}
