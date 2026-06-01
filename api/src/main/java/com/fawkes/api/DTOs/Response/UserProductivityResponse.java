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
public class UserProductivityResponse {

    private List<UserStats> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long userId;
        private String userName;
        private String departmentName;
        private long totalOrders;
        private long completedOrders;
        private long pendingOrders;
        private long totalTickets;
        private long openTickets;
        private BigDecimal totalValueManaged;
        private double completionRatePct;
        private String performanceLabel;  // "HIGH" | "NORMAL" | "LOW"
    }
}
