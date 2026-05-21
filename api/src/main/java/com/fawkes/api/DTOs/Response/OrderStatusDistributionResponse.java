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
public class OrderStatusDistributionResponse {

    private List<StatusSlice> orders;
    private List<StatusSlice> purchaseOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusSlice {
        private String status;       // valor interno: "confirmed"
        private String statusLabel;  // valor exibido: "Aprovado"
        private long count;
        private double percentage;
        private String colorHex;
    }
}