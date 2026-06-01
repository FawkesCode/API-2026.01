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
public class MonthlyMovementResponse {

    private List<MonthlyPoint> orders;
    private List<MonthlyPoint> purchaseOrders;
    private List<MonthlyPoint> tickets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyPoint {
        private int year;
        private int month;
        private String monthLabel;   // "Jan", "Fev", ...
        private long count;
        private BigDecimal totalValue;
    }

    public static String monthLabel(int month) {
        return switch (month) {
            case 1  -> "Jan"; case 2  -> "Fev"; case 3  -> "Mar";
            case 4  -> "Abr"; case 5  -> "Mai"; case 6  -> "Jun";
            case 7  -> "Jul"; case 8  -> "Ago"; case 9  -> "Set";
            case 10 -> "Out"; case 11 -> "Nov"; default -> "Dez";
        };
    }
}
