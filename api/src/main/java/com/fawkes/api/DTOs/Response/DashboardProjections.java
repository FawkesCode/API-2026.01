package com.fawkes.api.DTOs.Response;

import java.math.BigDecimal;

/**
 * Projections de leitura usadas nas queries JPQL do dashboard.
 * Cada interface é mapeada diretamente por Spring Data sem precisar de @Entity.
 */
public final class DashboardProjections {

    private DashboardProjections() {}

    // ─── Movimentação Mensal ───────────────────────────────────────────
    public interface MonthlyCountProjection {
        Integer getYear();
        Integer getMonth();
        Long getCount();
        BigDecimal getTotalValue();
    }

    // ─── Status Pedidos ────────────────────────────────────────────────
    public interface StatusCountProjection {
        String getStatus();
        Long getCount();
    }

    // ─── Top Fornecedores ──────────────────────────────────────────────
    public interface SupplierRankProjection {
        Long getSupplierId();
        String getSupplierName();
        Boolean getIsActive();
        Long getTotalOrders();
        BigDecimal getTotalValue();
        Long getCancelledOrders();
    }

    // ─── Curva ABC ─────────────────────────────────────────────────────
    public interface AbcProductProjection {
        Long getProductId();
        String getProductName();
        String getProductType();
        String getSupplierName();
        BigDecimal getUnitValue();
        Long getTotalQuantityOrdered();
        BigDecimal getTotalValueOrdered();
    }

    // ─── Produtividade Usuários ────────────────────────────────────────
    public interface UserProductivityProjection {
        Long getUserId();
        String getUserName();
        String getDepartmentName();
        Long getTotalOrders();
        Long getCompletedOrders();
        Long getPendingOrders();
        BigDecimal getTotalValueManaged();
    }

    // ─── Estoque Crítico ───────────────────────────────────────────────
    public interface CriticalStockProjection {
        Long getProductId();
        String getProductName();
        String getProductType();
        String getSupplierName();
        String getStockName();
        Integer getAvailableQuantity();
        Integer getMinStockQuantity();
        Integer getMaxStockQuantity();
    }

    // ─── KPI Agregados ─────────────────────────────────────────────────
    public interface OrderKpiProjection {
        Long getTotalOrders();
        BigDecimal getTotalValue();
        Long getPendingCount();
        Long getProcessingCount();
        Long getCompletedCount();
        Long getCancelledCount();
    }

    public interface PurchaseOrderKpiProjection {
        Long getTotalOrders();
        BigDecimal getTotalValue();
        Long getDraftCount();
        Long getPendingCount();
        Long getConfirmedCount();
        Long getShippedCount();
        Long getReceivedCount();
        Long getCancelledCount();
    }

    // ─── Últimos Pedidos (tabela do dashboard) ─────────────────────────
    public interface RecentOrderProjection {
        Long getId();
        String getUserName();
        String getDepartmentName();
        BigDecimal getTotalValue();
        String getStatus();
        java.time.LocalDateTime getCreatedAt();
        java.time.LocalDateTime getOrderDate();
    }

    public interface RecentPurchaseOrderProjection {
        Long getId();
        String getSupplierName();
        String getUserName();
        BigDecimal getTotalValue();
        String getStatus();
        java.time.LocalDateTime getCreatedAt();
        java.time.LocalDateTime getOrderDate();
        java.time.LocalDateTime getExpectedDeliveryDate();
    }
}
