package com.fawkes.api.Repositories;

import com.fawkes.api.DTOs.Response.DashboardProjections.*;
import com.fawkes.api.Entities.SupplierStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardStockRepository extends JpaRepository<SupplierStock, Long> {

    // ── Produtos com estoque abaixo do mínimo ─────────────────────────
    @Query("""
        SELECT
            ss.product.id          AS productId,
            ss.product.productName AS productName,
            ss.product.productType AS productType,
            ss.supplier.supplierName AS supplierName,
            ss.product.stock.stockName AS stockName,
            ss.availableQuantity   AS availableQuantity,
            ss.minStockQuantity    AS minStockQuantity,
            ss.maxStockQuantity    AS maxStockQuantity
        FROM SupplierStock ss
        WHERE ss.isActive = true
          AND ss.minStockQuantity IS NOT NULL
          AND ss.availableQuantity < ss.minStockQuantity
          AND (:supplierId IS NULL OR ss.supplier.id = :supplierId)
        ORDER BY ss.availableQuantity ASC
    """)
    List<CriticalStockProjection> fetchLowStockProducts(
            @Param("supplierId") Long supplierId);

    // ── Produtos com estoque zerado ────────────────────────────────────
    @Query("""
        SELECT
            ss.product.id          AS productId,
            ss.product.productName AS productName,
            ss.product.productType AS productType,
            ss.supplier.supplierName AS supplierName,
            ss.product.stock.stockName AS stockName,
            ss.availableQuantity   AS availableQuantity,
            ss.minStockQuantity    AS minStockQuantity,
            ss.maxStockQuantity    AS maxStockQuantity
        FROM SupplierStock ss
        WHERE ss.isActive = true
          AND ss.availableQuantity = 0
          AND (:supplierId IS NULL OR ss.supplier.id = :supplierId)
        ORDER BY ss.product.productName ASC
    """)
    List<CriticalStockProjection> fetchOutOfStockProducts(
            @Param("supplierId") Long supplierId);

    // ── Contagens para KPI ─────────────────────────────────────────────
    @Query("""
        SELECT COUNT(ss) FROM SupplierStock ss
        WHERE ss.isActive = true
          AND ss.minStockQuantity IS NOT NULL
          AND ss.availableQuantity < ss.minStockQuantity
    """)
    long countLowStockProducts();

    @Query("""
        SELECT COUNT(ss) FROM SupplierStock ss
        WHERE ss.isActive = true AND ss.availableQuantity = 0
    """)
    long countOutOfStockProducts();

    // ── Fornecedores ativos (via SupplierStock) ────────────────────────
    @Query("""
        SELECT COUNT(DISTINCT ss.supplier.id)
        FROM SupplierStock ss
        WHERE ss.isActive = true AND ss.supplier.isActive = true
    """)
    long countActiveSuppliers();

    // ── Alertas: produtos zerados por fornecedor ───────────────────────
    @Query("""
        SELECT ss FROM SupplierStock ss
        WHERE ss.isActive = true
          AND ss.availableQuantity = 0
        ORDER BY ss.product.productName ASC
    """)
    List<SupplierStock> fetchOutOfStockEntities();

    // ── Alertas: abaixo do mínimo por fornecedor ───────────────────────
    @Query("""
        SELECT ss FROM SupplierStock ss
        WHERE ss.isActive = true
          AND ss.minStockQuantity IS NOT NULL
          AND ss.availableQuantity < ss.minStockQuantity
        ORDER BY ss.availableQuantity ASC
    """)
    List<SupplierStock> fetchLowStockEntities();

    @Query("""
    SELECT COUNT(ps) FROM ProductStock ps
    WHERE ps.minStockQuantity IS NOT NULL
      AND ps.minStockQuantity > 0
      AND ps.currentStockQuantity < ps.minStockQuantity
    """)
    long countLowStockProductsFromProductStock();


}
