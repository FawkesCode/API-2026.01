package com.fawkes.api.Repositories;

import com.fawkes.api.DTOs.Response.DashboardProjections.*;
import com.fawkes.api.Entities.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardOrderRepository extends JpaRepository<Orders, Long> {

    // ── KPI agregados ──────────────────────────────────────────────────
    @Query("""
        SELECT
            COUNT(o)                                                           AS totalOrders,
            COALESCE(SUM(o.totalValue), 0)                                     AS totalValue,
            SUM(CASE WHEN o.status = 'pendente'    THEN 1 ELSE 0 END)          AS pendingCount,
            SUM(CASE WHEN o.status = 'processando' THEN 1 ELSE 0 END)          AS processingCount,
            SUM(CASE WHEN o.status = 'concluido'   THEN 1 ELSE 0 END)          AS completedCount,
            SUM(CASE WHEN o.status = 'cancelado'   THEN 1 ELSE 0 END)          AS cancelledCount
        FROM Orders o
        WHERE o.createdAt BETWEEN :from AND :to
          AND (:departmentId IS NULL OR o.department.id = :departmentId)
          AND (:userId       IS NULL OR o.user.id       = :userId)
    """)
    OrderKpiProjection fetchOrderKpis(
            @Param("from")         LocalDateTime from,
            @Param("to")           LocalDateTime to,
            @Param("departmentId") Long departmentId,
            @Param("userId")       Long userId);

    // ── Movimentação mensal ────────────────────────────────────────────
    @Query("""
        SELECT
            YEAR(o.createdAt)  AS year,
            MONTH(o.createdAt) AS month,
            COUNT(o)           AS count,
            COALESCE(SUM(o.totalValue), 0) AS totalValue
        FROM Orders o
        WHERE o.createdAt BETWEEN :from AND :to
          AND (:departmentId IS NULL OR o.department.id = :departmentId)
        GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
        ORDER BY year, month
    """)
    List<MonthlyCountProjection> fetchMonthlyOrders(
            @Param("from")         LocalDateTime from,
            @Param("to")           LocalDateTime to,
            @Param("departmentId") Long departmentId);

    // ── Distribuição por status ────────────────────────────────────────
    @Query("""
        SELECT CAST(o.status AS string) AS status, COUNT(o) AS count
        FROM Orders o
        WHERE o.createdAt BETWEEN :from AND :to
          AND (:departmentId IS NULL OR o.department.id = :departmentId)
        GROUP BY o.status
    """)
    List<StatusCountProjection> fetchStatusDistribution(
            @Param("from")         LocalDateTime from,
            @Param("to")           LocalDateTime to,
            @Param("departmentId") Long departmentId);

    // ── Últimos pedidos (tabela) ───────────────────────────────────────
    @Query("""
        SELECT
            o.id                        AS id,
            o.user.userName             AS userName,
            o.department.departamentName AS departmentName,
            o.totalValue                AS totalValue,
            CAST(o.status AS string)    AS status,
            o.createdAt                 AS createdAt,
            o.orderDate                 AS orderDate
        FROM Orders o
        WHERE (:departmentId IS NULL OR o.department.id = :departmentId)
          AND (:userId       IS NULL OR o.user.id       = :userId)
          AND (:status       IS NULL OR CAST(o.status AS string) = :status)
        ORDER BY o.createdAt DESC
    """)
    Page<RecentOrderProjection> fetchRecentOrders(
            @Param("departmentId") Long departmentId,
            @Param("userId")       Long userId,
            @Param("status")       String status,
            Pageable pageable);

    // ── Produtividade por usuário ──────────────────────────────────────
    @Query("""
        SELECT
            o.user.id                        AS userId,
            o.user.userName                  AS userName,
            o.user.departments.departamentName AS departmentName,
            COUNT(o)                                                          AS totalOrders,
            SUM(CASE WHEN o.status = 'concluido' THEN 1 ELSE 0 END)          AS completedOrders,
            SUM(CASE WHEN o.status = 'pendente'  THEN 1 ELSE 0 END)          AS pendingOrders,
            COALESCE(SUM(o.totalValue), 0)                                    AS totalValueManaged
        FROM Orders o
        WHERE o.createdAt BETWEEN :from AND :to
        GROUP BY o.user.id, o.user.userName, o.user.departments.departamentName
        ORDER BY totalOrders DESC
    """)
    List<UserProductivityProjection> fetchUserProductivity(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    // ── Alertas: pedidos pendentes há mais de N dias ───────────────────
    @Query("""
        SELECT o FROM Orders o
        WHERE o.status = 'pendente'
          AND o.createdAt <= :threshold
          AND (:departmentId IS NULL OR o.department.id = :departmentId)
        ORDER BY o.createdAt ASC
    """)
    List<Orders> fetchStuckPendingOrders(
            @Param("threshold")    LocalDateTime threshold,
            @Param("departmentId") Long departmentId);

    // ── Contagem rápida por status e período ──────────────────────────
    @Query("""
        SELECT COUNT(o) FROM Orders o
        WHERE CAST(o.status AS string) = :status
          AND o.createdAt BETWEEN :from AND :to
    """)
    long countByStatusAndPeriod(
            @Param("status") String status,
            @Param("from")   LocalDateTime from,
            @Param("to")     LocalDateTime to);
}