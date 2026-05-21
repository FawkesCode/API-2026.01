package com.fawkes.api.DTOs.Request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class DashboardFilterRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime to;

    private Long supplierId;
    private Long userId;
    private Long departmentId;
    private String orderStatus;          // Orders.Status name
    private String purchaseOrderStatus;  // PurchaseOrder.Status name

    // Conveniência: período pré-definido (sobrescreve from/to se informado)
    private Period period;

    public enum Period { TODAY, LAST_7_DAYS, THIS_MONTH, LAST_MONTH, LAST_3_MONTHS, THIS_YEAR }

    public LocalDateTime resolvedFrom() {
        if (period != null) {
            return switch (period) {
                case TODAY         -> LocalDateTime.now().toLocalDate().atStartOfDay();
                case LAST_7_DAYS   -> LocalDateTime.now().minusDays(7);
                case THIS_MONTH    -> LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
                case LAST_MONTH    -> LocalDateTime.now().minusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
                case LAST_3_MONTHS -> LocalDateTime.now().minusMonths(3);
                case THIS_YEAR     -> LocalDateTime.now().withDayOfYear(1).toLocalDate().atStartOfDay();
            };
        }
        return from != null ? from : LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
    }

    public LocalDateTime resolvedTo() {
        if (period != null && period == Period.LAST_MONTH) {
            return LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay().minusSeconds(1);
        }
        return to != null ? to : LocalDateTime.now();
    }

    // Retorna o início do período equivalente anterior (para comparação de tendência)
    public LocalDateTime previousPeriodFrom() {
        long days = java.time.temporal.ChronoUnit.DAYS.between(resolvedFrom(), resolvedTo());
        return resolvedFrom().minusDays(days == 0 ? 1 : days);
    }

    public LocalDateTime previousPeriodTo() {
        return resolvedFrom().minusSeconds(1);
    }
}
