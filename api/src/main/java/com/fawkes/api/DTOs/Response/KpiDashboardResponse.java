package com.fawkes.api.DTOs.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KpiDashboardResponse {

    // Pedidos internos (Orders)
    private KpiResponse totalOrdersThisMonth;
    private KpiResponse pendingOrders;
    private KpiResponse processingOrders;
    private KpiResponse completedOrders;
    private KpiResponse cancelledOrders;

    // Purchase Orders
    private KpiResponse totalPurchaseOrdersThisMonth;
    private KpiResponse purchaseOrdersPending;
    private KpiResponse purchaseOrdersConfirmed;
    private KpiResponse purchaseOrdersShipped;
    private KpiResponse purchaseOrdersReceived;
    private KpiResponse purchaseOrdersCancelled;

    // Financeiro
    private KpiResponse totalValueMovedThisMonth;
    private KpiResponse totalPurchaseValueThisMonth;

    // Estoque
    private KpiResponse productsLowStock;
    private KpiResponse productsOutOfStock;
    private KpiResponse activeSuppliers;

    // Operacional
    private KpiResponse avgProcessingTimeDays;
    private KpiResponse completionRatePct;
    private KpiResponse delayRatePct;

    // Tickets
    private KpiResponse openTickets;
    private KpiResponse totalTicketsThisMonth;
}
