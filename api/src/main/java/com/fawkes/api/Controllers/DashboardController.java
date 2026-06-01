package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.Request.DashboardFilterRequest;
import com.fawkes.api.DTOs.Response.*;
import com.fawkes.api.Services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // ─── Filtro global montado a partir dos query params ──────────────

    private DashboardFilterRequest buildFilter(
            LocalDateTime from,
            LocalDateTime to,
            DashboardFilterRequest.Period period,
            Long supplierId,
            Long userId,
            Long departmentId,
            String orderStatus,
            String purchaseOrderStatus) {

        DashboardFilterRequest f = new DashboardFilterRequest();
        f.setFrom(from);
        f.setTo(to);
        f.setPeriod(period);
        f.setSupplierId(supplierId);
        f.setUserId(userId);
        f.setDepartmentId(departmentId);
        f.setOrderStatus(orderStatus);
        f.setPurchaseOrderStatus(purchaseOrderStatus);
        return f;
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/kpis
    // Acesso: DIRECTOR, MANAGER
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    public ResponseEntity<KpiDashboardResponse> getKpis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) DashboardFilterRequest.Period period,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long departmentId) {

        DashboardFilterRequest f = buildFilter(from, to, period, supplierId, userId, departmentId, null, null);
        return ResponseEntity.ok(dashboardService.getKpis(f));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/movimentacao-mensal
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/movimentacao-mensal")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','OPERATIONAL')")
    public ResponseEntity<MonthlyMovementResponse> getMonthlyMovement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) DashboardFilterRequest.Period period,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long departmentId) {

        DashboardFilterRequest f = buildFilter(from, to, period, supplierId, null, departmentId, null, null);
        return ResponseEntity.ok(dashboardService.getMonthlyMovement(f));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/status-pedidos
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/status-pedidos")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','OPERATIONAL')")
    public ResponseEntity<OrderStatusDistributionResponse> getStatusDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) DashboardFilterRequest.Period period,
            @RequestParam(required = false) Long departmentId) {

        DashboardFilterRequest f = buildFilter(from, to, period, null, null, departmentId, null, null);
        return ResponseEntity.ok(dashboardService.getStatusDistribution(f));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/top-fornecedores
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/top-fornecedores")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    public ResponseEntity<TopSupplierResponse> getTopSuppliers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) DashboardFilterRequest.Period period,
            @RequestParam(defaultValue = "10") int limit) {

        DashboardFilterRequest f = buildFilter(from, to, period, null, null, null, null, null);
        return ResponseEntity.ok(dashboardService.getTopSuppliers(f, limit));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/produtos-criticos
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/produtos-criticos")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','OPERATIONAL')")
    public ResponseEntity<CriticalProductResponse> getCriticalProducts(
            @RequestParam(required = false) Long supplierId) {

        DashboardFilterRequest f = new DashboardFilterRequest();
        f.setSupplierId(supplierId);
        return ResponseEntity.ok(dashboardService.getCriticalProducts(f));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/ultimos-pedidos
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/ultimos-pedidos")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','OPERATIONAL')")
    public ResponseEntity<Page<RecentOrderResponse>> getRecentOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        DashboardFilterRequest f = buildFilter(null, null, null, null, userId, departmentId, status, null);
        return ResponseEntity.ok(dashboardService.getRecentOrders(f, page, size));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/ultimas-ordens-compra
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/ultimas-ordens-compra")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','OPERATIONAL')")
    public ResponseEntity<Page<RecentOrderResponse>> getRecentPurchaseOrders(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        DashboardFilterRequest f = buildFilter(null, null, null, supplierId, userId, null, null, status);
        return ResponseEntity.ok(dashboardService.getRecentPurchaseOrders(f, page, size));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/alertas
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/alertas")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','OPERATIONAL')")
    public ResponseEntity<AlertResponse> getAlerts(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long departmentId) {

        DashboardFilterRequest f = new DashboardFilterRequest();
        f.setSupplierId(supplierId);
        f.setDepartmentId(departmentId);
        return ResponseEntity.ok(dashboardService.getAlerts(f));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/curva-abc
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/curva-abc")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    public ResponseEntity<CurvaAbcResponse> getCurvaAbc(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) DashboardFilterRequest.Period period) {

        DashboardFilterRequest f = buildFilter(from, to, period, null, null, null, null, null);
        return ResponseEntity.ok(dashboardService.getCurvaAbc(f));
    }

    // ════════════════════════════════════════════════════════════════════
    // GET /dashboard/produtividade-funcionarios
    // ════════════════════════════════════════════════════════════════════
    @GetMapping("/produtividade-funcionarios")
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    public ResponseEntity<UserProductivityResponse> getUserProductivity(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) DashboardFilterRequest.Period period) {

        DashboardFilterRequest f = buildFilter(from, to, period, null, null, null, null, null);
        return ResponseEntity.ok(dashboardService.getUserProductivity(f));
    }
}
