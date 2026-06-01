package com.fawkes.api.DTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {

    private List<AlertItem> stock;
    private List<AlertItem> orders;
    private List<AlertItem> operational;
    private int totalCritical;
    private int totalHigh;
    private int totalAlerts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertItem {
        private String id;
        private AlertType type;
        private Priority priority;
        private String title;
        private String description;
        private String entityId;
        private String entityName;
        private String actionLabel;
        private String actionEndpoint;
        private LocalDateTime detectedAt;
        private String colorHex;

        public enum AlertType {
            STOCK_LOW, STOCK_OUT, STOCK_NO_MOVEMENT,
            ORDER_PENDING_APPROVAL, ORDER_OVERDUE, ORDER_STUCK,
            PURCHASE_DELAYED, PURCHASE_OVERDUE,
            DEMAND_SPIKE, OPERATIONAL_BOTTLENECK
        }

        public enum Priority { CRITICAL, HIGH, MEDIUM, LOW }
    }
}
