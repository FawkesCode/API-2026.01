package com.fawkes.api.Repositories;

import com.fawkes.api.DTOs.Response.DashboardProjections.*;
import com.fawkes.api.Entities.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardPurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    // ── KPIs ───────────────────────────────────────────────────────────
    @Query("""
        SELECT
            COUNT(p)                                                              AS totalOrders,
            COALESCE(SUM(p.totalValue), 0)                                        AS totalValue,
            SUM(CASE WHEN p.status = 'draft'      THEN 1 ELSE 0 END)             AS draftCount,
            SUM(CASE WHEN p.status = 'pending'    THEN 1 ELSE 0 END)             AS pendingCount,
            SUM(CASE WHEN p.status = 'confirmed'  THEN 1 ELSE 0 END)             AS confirmedCount,
            SUM(CASE WHEN p.status = 'shipped'    THEN 1 ELSE 0 END)             AS shippedCount,
            SUM(CASE WHEN p.status = 'received'   THEN 1 ELSE 0 END)             AS receivedCount,
            SUM(CASE WHEN p.status = 'cancelled'  THEN 1 ELSE 0 END)             AS cancelledCount
        FROM PurchaseOrder p
        WHERE p.createdAt BETWEEN :from AND :to
          AND (:supplierId IS NULL OR p.supplier.id  = :supplierId)
          AND (:userId     IS NULL OR p.createdBy.id = :userId)
    """)
    PurchaseOrderKpiProjection fetchPurchaseOrderKpis(
            @Param("from")       LocalDateTime from,
            @Param("to")         LocalDateTime to,
            @Param("supplierId") Long supplierId,
            @Param("userId")     Long userId);

    // ── Movimentação mensal ────────────────────────────────────────────
    @Query("""
        SELECT
            YEAR(p.createdAt)  AS year,
            MONTH(p.createdAt) AS month,
            COUNT(p)           AS count,
            COALESCE(SUM(p.totalValue), 0) AS totalValue
        FROM PurchaseOrder p
        WHERE p.createdAt BETWEEN :from AND :to
          AND (:supplierId IS NULL OR p.supplier.id = :supplierId)
        GROUP BY YEAR(p.createdAt), MONTH(p.createdAt)
        ORDER BY year, month
    """)
    List<MonthlyCountProjection> fetchMonthlyPurchaseOrders(
            @Param("from")       LocalDateTime from,
            @Param("to")         LocalDateTime to,
            @Param("supplierId") Long supplierId);

    // ── Distribuição por status ────────────────────────────────────────
    @Query("""
        SELECT CAST(p.status AS string) AS status, COUNT(p) AS count
        FROM PurchaseOrder p
        WHERE p.createdAt BETWEEN :from AND :to
        GROUP BY p.status
    """)
    List<StatusCountProjection> fetchStatusDistribution(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    // ── Top fornecedores por volume ────────────────────────────────────
    @Query("""
        SELECT
            p.supplier.id           AS supplierId,
            p.supplier.supplierName AS supplierName,
            p.supplier.isActive     AS isActive,
            COUNT(p)                AS totalOrders,
            COALESCE(SUM(p.totalValue), 0) AS totalValue,
            SUM(CASE WHEN p.status = 'cancelled' THEN 1 ELSE 0 END) AS cancelledOrders
        FROM PurchaseOrder p
        WHERE p.createdAt BETWEEN :from AND :to
        GROUP BY p.supplier.id, p.supplier.supplierName, p.supplier.isActive
        ORDER BY totalValue DESC
    """)
    List<SupplierRankProjection> fetchTopSuppliersByVolume(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to,
            Pageable pageable);

    // ── Fornecedores problemáticos — HAVING com expressão completa ────
    @Query("""
        SELECT
            p.supplier.id           AS supplierId,
            p.supplier.supplierName AS supplierName,
            p.supplier.isActive     AS isActive,
            COUNT(p)                AS totalOrders,
            COALESCE(SUM(p.totalValue), 0) AS totalValue,
            SUM(CASE WHEN p.status = 'cancelled' THEN 1 ELSE 0 END) AS cancelledOrders
        FROM PurchaseOrder p
        WHERE p.createdAt BETWEEN :from AND :to
        GROUP BY p.supplier.id, p.supplier.supplierName, p.supplier.isActive
        HAVING SUM(CASE WHEN p.status = 'cancelled' THEN 1 ELSE 0 END) > 0
        ORDER BY SUM(CASE WHEN p.status = 'cancelled' THEN 1 ELSE 0 END) DESC
    """)
    List<SupplierRankProjection> fetchProblematicSuppliers(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to,
            Pageable pageable);

    // ── Últimas purchase orders (tabela) ───────────────────────────────
    @Query("""
        SELECT
            p.id                    AS id,
            p.supplier.supplierName AS supplierName,
            p.createdBy.userName    AS userName,
            p.totalValue            AS totalValue,
            CAST(p.status AS string) AS status,
            p.createdAt             AS createdAt,
            p.orderDate             AS orderDate,
            p.expectedDeliveryDate  AS expectedDeliveryDate
        FROM PurchaseOrder p
        WHERE (:supplierId IS NULL OR p.supplier.id  = :supplierId)
          AND (:userId     IS NULL OR p.createdBy.id = :userId)
          AND (:status     IS NULL OR CAST(p.status AS string) = :status)
        ORDER BY p.createdAt DESC
    """)
    Page<RecentPurchaseOrderProjection> fetchRecentPurchaseOrders(
            @Param("supplierId") Long supplierId,
            @Param("userId")     Long userId,
            @Param("status")     String status,
            Pageable pageable);

    // ── Alertas: purchase orders com entrega vencida ───────────────────
    @Query("""
        SELECT p FROM PurchaseOrder p
        WHERE p.expectedDeliveryDate < :now
          AND p.status NOT IN ('received', 'cancelled')
          AND (:supplierId IS NULL OR p.supplier.id = :supplierId)
        ORDER BY p.expectedDeliveryDate ASC
    """)
    List<PurchaseOrder> fetchOverduePurchaseOrders(
            @Param("now")        LocalDateTime now,
            @Param("supplierId") Long supplierId);

    // ── Curva ABC ─────────────────────────────────────────────────────
    @Query("""
        SELECT
            i.product.id                     AS productId,
            i.product.productName            AS productName,
            i.product.productType            AS productType,
            i.product.suppliers.supplierName AS supplierName,
            i.product.unitValue              AS unitValue,
            COALESCE(SUM(i.quantity), 0)                         AS totalQuantityOrdered,
            COALESCE(SUM(i.quantity * i.product.unitValue), 0)   AS totalValueOrdered
        FROM PurchaseOrderItem i
        WHERE i.purchaseOrder.createdAt BETWEEN :from AND :to
          AND i.purchaseOrder.status != 'cancelled'
        GROUP BY i.product.id, i.product.productName, i.product.productType,
                 i.product.suppliers.supplierName, i.product.unitValue
        ORDER BY totalValueOrdered DESC
    """)
    List<AbcProductProjection> fetchProductsForCurvaAbc(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    // ── Tempo médio de processamento ──────────────────────────────────
    @Query(value = """
        SELECT AVG(DATEDIFF(po.updated_at, po.order_date))
        FROM TB_purchase_order po
        WHERE po.status = 'received'
          AND po.created_at BETWEEN :from AND :to
    """, nativeQuery = true)
    Double fetchAvgProcessingDays(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);
}