package com.fawkes.api.Repositories;

import com.fawkes.api.DTOs.Response.DashboardProjections.MonthlyCountProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.StatusCountProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.UserProductivityProjection;
import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Entities.TicketEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardTicketRepository extends JpaRepository<Ticket, Long> {

    // ── KPI: contagem total no período ────────────────────────────────
    @Query("""
        SELECT COUNT(t) FROM Ticket t
        WHERE t.createdAt BETWEEN :from AND :to
          AND (:userId       IS NULL OR t.usuario.id      = :userId)
          AND (:departmentId IS NULL OR t.departamento.id = :departmentId)
    """)
    long countTicketsInPeriod(
            @Param("from")         LocalDateTime from,
            @Param("to")           LocalDateTime to,
            @Param("userId")       Long userId,
            @Param("departmentId") Long departmentId);

    // ── KPI: tickets abertos ──────────────────────────────────────────
    @Query("""
        SELECT COUNT(t) FROM Ticket t
        WHERE t.status IN (
            com.fawkes.api.Entities.TicketEnum.pendente,
            com.fawkes.api.Entities.TicketEnum.processando
        )
        AND (:departmentId IS NULL OR t.departamento.id = :departmentId)
    """)
    long countOpenTickets(@Param("departmentId") Long departmentId);

    // ── Movimentação mensal ────────────────────────────────────────────
    @Query("""
        SELECT
            YEAR(t.createdAt)  AS year,
            MONTH(t.createdAt) AS month,
            COUNT(t)           AS count,
            COALESCE(SUM(t.valor), 0) AS totalValue
        FROM Ticket t
        WHERE t.createdAt BETWEEN :from AND :to
          AND (:departmentId IS NULL OR t.departamento.id = :departmentId)
        GROUP BY YEAR(t.createdAt), MONTH(t.createdAt)
        ORDER BY year, month
    """)
    List<MonthlyCountProjection> fetchMonthlyTickets(
            @Param("from")         LocalDateTime from,
            @Param("to")           LocalDateTime to,
            @Param("departmentId") Long departmentId);

    // ── Distribuição por status ────────────────────────────────────────
    @Query("""
        SELECT CAST(t.status AS string) AS status, COUNT(t) AS count
        FROM Ticket t
        WHERE t.createdAt BETWEEN :from AND :to
          AND (:departmentId IS NULL OR t.departamento.id = :departmentId)
        GROUP BY t.status
    """)
    List<StatusCountProjection> fetchStatusDistribution(
            @Param("from")         LocalDateTime from,
            @Param("to")           LocalDateTime to,
            @Param("departmentId") Long departmentId);

    // ── Produtividade por usuário ──────────────────────────────────────
    @Query("""
        SELECT
            t.usuario.id                              AS userId,
            t.usuario.userName                        AS userName,
            t.usuario.departments.departamentName     AS departmentName,
            COUNT(t)                                  AS totalOrders,
            SUM(CASE WHEN t.status = com.fawkes.api.Entities.TicketEnum.concluido
                     THEN 1 ELSE 0 END)               AS completedOrders,
            SUM(CASE WHEN t.status = com.fawkes.api.Entities.TicketEnum.pendente
                     THEN 1 ELSE 0 END)               AS pendingOrders,
            COALESCE(SUM(t.valor), 0)                 AS totalValueManaged
        FROM Ticket t
        WHERE t.createdAt BETWEEN :from AND :to
        GROUP BY t.usuario.id, t.usuario.userName, t.usuario.departments.departamentName
        ORDER BY totalOrders DESC
    """)
    List<UserProductivityProjection> fetchUserProductivityByTickets(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    // ── Alertas: tickets pendentes há mais de N dias ──────────────────
    @Query("""
        SELECT t FROM Ticket t
        WHERE t.status = com.fawkes.api.Entities.TicketEnum.pendente
          AND t.createdAt <= :threshold
          AND (:departmentId IS NULL OR t.departamento.id = :departmentId)
        ORDER BY t.createdAt ASC
    """)
    List<Ticket> fetchStuckPendingTickets(
            @Param("threshold")    LocalDateTime threshold,
            @Param("departmentId") Long departmentId);
}