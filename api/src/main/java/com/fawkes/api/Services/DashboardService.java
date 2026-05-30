package com.fawkes.api.Services;

import com.fawkes.api.DTOs.Request.DashboardFilterRequest;
import com.fawkes.api.DTOs.Response.AlertResponse;
import com.fawkes.api.DTOs.Response.AlertResponse.AlertItem;
import com.fawkes.api.DTOs.Response.AlertResponse.AlertItem.AlertType;
import com.fawkes.api.DTOs.Response.AlertResponse.AlertItem.Priority;
import com.fawkes.api.DTOs.Response.CriticalProductResponse;
import com.fawkes.api.DTOs.Response.CriticalProductResponse.CriticalItem;
import com.fawkes.api.DTOs.Response.CurvaAbcResponse;
import com.fawkes.api.DTOs.Response.CurvaAbcResponse.AbcItem;
import com.fawkes.api.DTOs.Response.DashboardProjections.AbcProductProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.CriticalStockProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.MonthlyCountProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.OrderKpiProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.PurchaseOrderKpiProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.RecentOrderProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.RecentPurchaseOrderProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.StatusCountProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.SupplierRankProjection;
import com.fawkes.api.DTOs.Response.DashboardProjections.UserProductivityProjection;
import com.fawkes.api.DTOs.Response.KpiDashboardResponse;
import com.fawkes.api.DTOs.Response.KpiResponse;
import com.fawkes.api.DTOs.Response.MonthlyMovementResponse;
import com.fawkes.api.DTOs.Response.MonthlyMovementResponse.MonthlyPoint;
import com.fawkes.api.DTOs.Response.OrderStatusDistributionResponse;
import com.fawkes.api.DTOs.Response.OrderStatusDistributionResponse.StatusSlice;
import com.fawkes.api.DTOs.Response.RecentOrderResponse;
import com.fawkes.api.DTOs.Response.TopSupplierResponse;
import com.fawkes.api.DTOs.Response.TopSupplierResponse.SupplierRankItem;
import com.fawkes.api.DTOs.Response.UserProductivityResponse;
import com.fawkes.api.DTOs.Response.UserProductivityResponse.UserStats;
import com.fawkes.api.Entities.Orders;
import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Entities.SupplierStock;
import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Repositories.DashboardOrderRepository;
import com.fawkes.api.Repositories.DashboardPurchaseOrderRepository;
import com.fawkes.api.Repositories.DashboardStockRepository;
import com.fawkes.api.Repositories.DashboardTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardOrderRepository         orderRepo;
    private final DashboardPurchaseOrderRepository purchaseRepo;
    private final DashboardStockRepository         stockRepo;
    private final DashboardTicketRepository        ticketRepo;

    // ════════════════════════════════════════════════════════════════════
    // Mapeamento de status inglês → português (para o frontend)
    // ════════════════════════════════════════════════════════════════════

    private static final Map<String, String> PURCHASE_STATUS_LABEL = Map.of(
            "draft",      "Rascunho",
            "pending",    "Pendente",
            "confirmed",  "Aprovado",
            "shipped",    "Em trânsito",
            "received",   "Recebido",
            "cancelled",  "Cancelado"
    );

    private static final Map<String, String> ORDER_STATUS_LABEL = Map.of(
            "pendente",    "Pendente",
            "processando", "Em processamento",
            "concluido",   "Concluído",
            "cancelado",   "Cancelado"
    );

    // ════════════════════════════════════════════════════════════════════
    // KPIs
    // ════════════════════════════════════════════════════════════════════

    public KpiDashboardResponse getKpis(DashboardFilterRequest f) {
        LocalDateTime from     = f.resolvedFrom();
        LocalDateTime to       = f.resolvedTo();
        LocalDateTime prevFrom = f.previousPeriodFrom();
        LocalDateTime prevTo   = f.previousPeriodTo();

        OrderKpiProjection curr  = orderRepo.fetchOrderKpis(from, to, f.getDepartmentId(), f.getUserId());
        OrderKpiProjection prev  = orderRepo.fetchOrderKpis(prevFrom, prevTo, f.getDepartmentId(), f.getUserId());

        PurchaseOrderKpiProjection pcurr = purchaseRepo.fetchPurchaseOrderKpis(from, to, f.getSupplierId(), f.getUserId());
        PurchaseOrderKpiProjection pprev = purchaseRepo.fetchPurchaseOrderKpis(prevFrom, prevTo, f.getSupplierId(), f.getUserId());

        long ticketsCurr   = ticketRepo.countTicketsInPeriod(from, to, f.getUserId(), f.getDepartmentId());
        long ticketsPrev   = ticketRepo.countTicketsInPeriod(prevFrom, prevTo, f.getUserId(), f.getDepartmentId());
        long openTickets   = ticketRepo.countOpenTickets(f.getDepartmentId());
        long lowStock      = stockRepo.countLowStockProducts();
        long outOfStock    = stockRepo.countOutOfStockProducts();
        long activeSuppliers = stockRepo.countActiveSuppliers();
        Double avgDays     = purchaseRepo.fetchAvgProcessingDays(from, to);

        double completionRate = calcRate(safeL(curr.getCompletedCount()), safeL(curr.getTotalOrders()));
        double prevCompletion = calcRate(safeL(prev.getCompletedCount()), safeL(prev.getTotalOrders()));
        double cancelRate     = calcRate(safeL(curr.getCancelledCount()), safeL(curr.getTotalOrders()));

        KpiDashboardResponse.KpiDashboardResponseBuilder b = KpiDashboardResponse.builder();

        // KPIs principais do layout: Pendentes / Aprovadas / Comprados / Recebidos
        b.purchaseOrdersPending(kpiWithStatus("purchaseOrdersPending", "Pendentes",
                safeL(pcurr.getPendingCount()), safeL(pprev.getPendingCount()),
                safeL(pcurr.getPendingCount()) > 10 ? "warning" : "ok"));
        b.purchaseOrdersConfirmed(kpi("purchaseOrdersConfirmed", "Aprovadas",
                safeL(pcurr.getConfirmedCount()), safeL(pprev.getConfirmedCount())));
        b.purchaseOrdersShipped(kpi("purchaseOrdersShipped", "Comprados",
                safeL(pcurr.getShippedCount()), safeL(pprev.getShippedCount())));
        b.purchaseOrdersReceived(kpi("purchaseOrdersReceived", "Recebidos",
                safeL(pcurr.getReceivedCount()), safeL(pprev.getReceivedCount())));

        // Demais KPIs
        b.totalOrdersThisMonth(kpi("totalOrdersThisMonth", "Pedidos no período",
                safeL(curr.getTotalOrders()), safeL(prev.getTotalOrders())));
        b.pendingOrders(kpiWithStatus("pendingOrders", "Pedidos pendentes",
                safeL(curr.getPendingCount()), safeL(prev.getPendingCount()),
                safeL(curr.getPendingCount()) > 10 ? "warning" : "ok"));
        b.processingOrders(kpi("processingOrders", "Em processamento",
                safeL(curr.getProcessingCount()), safeL(prev.getProcessingCount())));
        b.completedOrders(kpi("completedOrders", "Concluídos",
                safeL(curr.getCompletedCount()), safeL(prev.getCompletedCount())));
        b.cancelledOrders(kpiWithStatus("cancelledOrders", "Cancelados",
                safeL(curr.getCancelledCount()), safeL(prev.getCancelledCount()),
                cancelRate > 15 ? "critical" : cancelRate > 8 ? "warning" : "ok"));

        b.totalPurchaseOrdersThisMonth(kpi("totalPurchaseOrders", "Ordens de compra",
                safeL(pcurr.getTotalOrders()), safeL(pprev.getTotalOrders())));
        b.purchaseOrdersCancelled(kpi("purchaseOrdersCancelled", "OC canceladas",
                safeL(pcurr.getCancelledCount()), safeL(pprev.getCancelledCount())));

        // Financeiro — "Total gasto em pedidos" do painel Atenção
        b.totalValueMovedThisMonth(kpi("totalValueMoved", "Valor total pedidos",
                safeBD(curr.getTotalValue()), safeBD(prev.getTotalValue())));
        b.totalPurchaseValueThisMonth(kpi("totalPurchaseValue", "Total gasto em compras",
                safeBD(pcurr.getTotalValue()), safeBD(pprev.getTotalValue())));

        // Estoque
        b.productsLowStock(kpiWithStatus("productsLowStock", "Est. abaixo do mínimo",
                lowStock, 0L, lowStock > 0 ? "warning" : "ok"));
        b.productsOutOfStock(kpiWithStatus("productsOutOfStock", "Ruptura de estoque",
                outOfStock, 0L, outOfStock > 0 ? "critical" : "ok"));
        b.activeSuppliers(kpi("activeSuppliers", "Fornecedores ativos",
                activeSuppliers, activeSuppliers));

        // Operacional
        b.avgProcessingTimeDays(kpi("avgProcessingDays", "Tempo médio processamento (dias)",
                avgDays != null ? Math.round(avgDays * 10.0) / 10.0 : 0.0, 0.0));
        b.completionRatePct(kpiWithStatus("completionRate", "Taxa de conclusão (%)",
                round2(completionRate), round2(prevCompletion),
                completionRate >= 80 ? "ok" : completionRate >= 60 ? "warning" : "critical"));
        b.delayRatePct(kpiWithStatus("delayRate", "Taxa de cancelamento (%)",
                round2(cancelRate), 0.0,
                cancelRate < 8 ? "ok" : cancelRate < 15 ? "warning" : "critical"));

        // Tickets
        b.totalTicketsThisMonth(kpi("totalTickets", "Tickets no período",
                ticketsCurr, ticketsPrev));
        b.openTickets(kpiWithStatus("openTickets", "Tickets abertos",
                openTickets, 0L,
                openTickets > 20 ? "critical" : openTickets > 10 ? "warning" : "ok"));

        return b.build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Movimentação mensal
    // ════════════════════════════════════════════════════════════════════

    public MonthlyMovementResponse getMonthlyMovement(DashboardFilterRequest f) {
        // Se não vier filtro de período, mostra os últimos 12 meses
        LocalDateTime from = f.resolvedFrom() != null
                ? f.resolvedFrom()
                : LocalDateTime.now().minusMonths(12).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime to = f.resolvedTo() != null
                ? f.resolvedTo()
                : LocalDateTime.now();

        List<MonthlyCountProjection> orders   = orderRepo.fetchMonthlyOrders(
                from, to, f.getDepartmentId());
        List<MonthlyCountProjection> purchase = purchaseRepo.fetchMonthlyPurchaseOrders(
                from, to, f.getSupplierId());
        List<MonthlyCountProjection> tickets  = ticketRepo.fetchMonthlyTickets(
                from, to, f.getDepartmentId());

        return MonthlyMovementResponse.builder()
                .orders(toPoints(orders))
                .purchaseOrders(toPoints(purchase))
                .tickets(toPoints(tickets))
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Status dos pedidos
    // ════════════════════════════════════════════════════════════════════

    public OrderStatusDistributionResponse getStatusDistribution(DashboardFilterRequest f) {
        List<StatusCountProjection> orders   = orderRepo.fetchStatusDistribution(
                f.resolvedFrom(), f.resolvedTo(), f.getDepartmentId());
        List<StatusCountProjection> purchase = purchaseRepo.fetchStatusDistribution(
                f.resolvedFrom(), f.resolvedTo());

        return OrderStatusDistributionResponse.builder()
                .orders(toSlices(orders, ORDER_COLORS, ORDER_STATUS_LABEL))
                .purchaseOrders(toSlices(purchase, PURCHASE_COLORS, PURCHASE_STATUS_LABEL))
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Top fornecedores
    // ════════════════════════════════════════════════════════════════════

    public TopSupplierResponse getTopSuppliers(DashboardFilterRequest f, int limit) {
        PageRequest page = PageRequest.of(0, limit);

        List<SupplierRankProjection> byVolume    = purchaseRepo.fetchTopSuppliersByVolume(
                f.resolvedFrom(), f.resolvedTo(), page);
        List<SupplierRankProjection> problematic = purchaseRepo.fetchProblematicSuppliers(
                f.resolvedFrom(), f.resolvedTo(), page);

        List<SupplierRankItem> byVolumeItems = toRankItems(byVolume);

        return TopSupplierResponse.builder()
                .byVolume(byVolumeItems)
                .byOrderCount(byVolumeItems.stream()
                        .sorted(Comparator.comparingLong(SupplierRankItem::getTotalOrders).reversed())
                        .toList())
                .problematic(toRankItems(problematic))
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Produtos críticos
    // ════════════════════════════════════════════════════════════════════

    public CriticalProductResponse getCriticalProducts(DashboardFilterRequest f) {
        return CriticalProductResponse.builder()
                .lowStock(toCriticalItems(stockRepo.fetchLowStockProducts(f.getSupplierId())))
                .outOfStock(toCriticalItems(stockRepo.fetchOutOfStockProducts(f.getSupplierId())))
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Últimos pedidos — tabela do dashboard (com status em português)
    // ════════════════════════════════════════════════════════════════════

    public Page<RecentOrderResponse> getRecentOrders(DashboardFilterRequest f, int page, int size) {
        return orderRepo.fetchRecentOrders(
                        f.getDepartmentId(), f.getUserId(), f.getOrderStatus(), PageRequest.of(page, size))
                .map(this::toRecentOrderResponse);
    }

    public Page<RecentOrderResponse> getRecentPurchaseOrders(DashboardFilterRequest f, int page, int size) {
        return purchaseRepo.fetchRecentPurchaseOrders(
                        f.getSupplierId(), f.getUserId(), f.getPurchaseOrderStatus(), PageRequest.of(page, size))
                .map(this::toRecentPurchaseResponse);
    }

    // ════════════════════════════════════════════════════════════════════
    // Alertas — inclui "Complicações com Pedidos" do painel Atenção
    // ════════════════════════════════════════════════════════════════════

    public AlertResponse getAlerts(DashboardFilterRequest f) {
        List<AlertItem> stockAlerts       = buildStockAlerts();
        List<AlertItem> orderAlerts       = buildOrderAlerts(f);
        List<AlertItem> operationalAlerts = buildOperationalAlerts(f);

        long critical = Stream.of(stockAlerts, orderAlerts, operationalAlerts)
                .flatMap(List::stream)
                .filter(a -> a.getPriority() == Priority.CRITICAL).count();
        long high = Stream.of(stockAlerts, orderAlerts, operationalAlerts)
                .flatMap(List::stream)
                .filter(a -> a.getPriority() == Priority.HIGH).count();

        return AlertResponse.builder()
                .stock(stockAlerts)
                .orders(orderAlerts)
                .operational(operationalAlerts)
                .totalCritical((int) critical)
                .totalHigh((int) high)
                .totalAlerts(stockAlerts.size() + orderAlerts.size() + operationalAlerts.size())
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Curva ABC
    // ════════════════════════════════════════════════════════════════════

    public CurvaAbcResponse getCurvaAbc(DashboardFilterRequest f) {
        List<AbcProductProjection> raw = purchaseRepo.fetchProductsForCurvaAbc(
                f.resolvedFrom(), f.resolvedTo());

        if (raw.isEmpty()) {
            return CurvaAbcResponse.builder().items(List.of())
                    .totalA(0).totalB(0).totalC(0)
                    .pctValueA(0).pctValueB(0).pctValueC(0)
                    .build();
        }

        BigDecimal totalGeral = raw.stream()
                .map(AbcProductProjection::getTotalValueOrdered)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<AbcItem> items = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;
        int rank = 1;
        long totalA = 0, totalB = 0, totalC = 0;

        for (AbcProductProjection p : raw) {
            double pct = totalGeral.compareTo(BigDecimal.ZERO) == 0 ? 0
                    : p.getTotalValueOrdered().multiply(BigDecimal.valueOf(100))
                    .divide(totalGeral, 2, RoundingMode.HALF_UP).doubleValue();
            cumulative = cumulative.add(BigDecimal.valueOf(pct));
            double cumPct = cumulative.doubleValue();

            String curva = cumPct <= 80 ? "A" : cumPct <= 95 ? "B" : "C";
            if ("A".equals(curva)) totalA++;
            else if ("B".equals(curva)) totalB++;
            else totalC++;

            items.add(AbcItem.builder()
                    .rank(rank++)
                    .productId(p.getProductId())
                    .productName(p.getProductName())
                    .productType(p.getProductType())
                    .supplierName(p.getSupplierName())
                    .unitValue(p.getUnitValue())
                    .totalQuantityOrdered(p.getTotalQuantityOrdered().intValue())
                    .totalValueOrdered(p.getTotalValueOrdered())
                    .percentageOfTotal(round2(pct))
                    .cumulativePercentage(round2(cumPct))
                    .curva(curva)
                    .build());
        }

        return CurvaAbcResponse.builder()
                .items(items)
                .totalA(totalA).totalB(totalB).totalC(totalC)
                .pctValueA(80.0).pctValueB(15.0).pctValueC(5.0)
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Produtividade por usuário
    // ════════════════════════════════════════════════════════════════════

    public UserProductivityResponse getUserProductivity(DashboardFilterRequest f) {
        List<UserProductivityProjection> fromOrders  = orderRepo.fetchUserProductivity(
                f.resolvedFrom(), f.resolvedTo());
        List<UserProductivityProjection> fromTickets = ticketRepo.fetchUserProductivityByTickets(
                f.resolvedFrom(), f.resolvedTo());

        Map<Long, UserStats> combined = new LinkedHashMap<>();

        for (UserProductivityProjection p : fromOrders) {
            double rate = calcRate(safeL(p.getCompletedOrders()), safeL(p.getTotalOrders()));
            combined.put(p.getUserId(), UserStats.builder()
                    .userId(p.getUserId())
                    .userName(p.getUserName())
                    .departmentName(p.getDepartmentName())
                    .totalOrders(safeL(p.getTotalOrders()))
                    .completedOrders(safeL(p.getCompletedOrders()))
                    .pendingOrders(safeL(p.getPendingOrders()))
                    .totalTickets(0L)
                    .openTickets(0L)
                    .totalValueManaged(safeBD(p.getTotalValueManaged()))
                    .completionRatePct(round2(rate))
                    .performanceLabel(rate >= 80 ? "HIGH" : rate >= 50 ? "NORMAL" : "LOW")
                    .build());
        }

        for (UserProductivityProjection p : fromTickets) {
            combined.merge(p.getUserId(),
                    UserStats.builder()
                            .userId(p.getUserId())
                            .userName(p.getUserName())
                            .departmentName(p.getDepartmentName())
                            .totalOrders(0L).completedOrders(0L).pendingOrders(0L)
                            .totalTickets(safeL(p.getTotalOrders()))
                            .openTickets(safeL(p.getPendingOrders()))
                            .totalValueManaged(safeBD(p.getTotalValueManaged()))
                            .completionRatePct(0).performanceLabel("NORMAL")
                            .build(),
                    (existing, incoming) -> {
                        existing.setTotalTickets(incoming.getTotalTickets());
                        existing.setOpenTickets(incoming.getOpenTickets());
                        existing.setTotalValueManaged(
                                existing.getTotalValueManaged().add(incoming.getTotalValueManaged()));
                        return existing;
                    });
        }

        return UserProductivityResponse.builder()
                .items(new ArrayList<>(combined.values()))
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Alertas — helpers privados
    // ════════════════════════════════════════════════════════════════════

    private List<AlertItem> buildStockAlerts() {
        List<AlertItem> alerts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Ruptura total — aparece no painel "Atenção" como produto zerado
        for (SupplierStock ss : stockRepo.fetchOutOfStockEntities()) {
            alerts.add(AlertItem.builder()
                    .id("STOCK_OUT_" + ss.getId())
                    .type(AlertType.STOCK_OUT)
                    .priority(Priority.CRITICAL)
                    .title("Ruptura de estoque")
                    .description(ss.getProduct().getProductName() + " — estoque zerado")
                    .entityId(String.valueOf(ss.getProduct().getId()))
                    .entityName(ss.getProduct().getProductName())
                    .actionLabel("Ver produto")
                    .actionEndpoint("/products/" + ss.getProduct().getId())
                    .detectedAt(now)
                    .colorHex("#E24B4A")
                    .build());
        }

        // Estoque baixo — aparece no painel "Atenção" como "perto do mínimo"
        for (SupplierStock ss : stockRepo.fetchLowStockEntities()) {
            if (ss.getAvailableQuantity() != null && ss.getAvailableQuantity() > 0) {
                boolean isCritical = ss.getMinStockQuantity() != null
                        && ss.getAvailableQuantity() < ss.getMinStockQuantity() / 2;
                alerts.add(AlertItem.builder()
                        .id("STOCK_LOW_" + ss.getId())
                        .type(AlertType.STOCK_LOW)
                        .priority(isCritical ? Priority.HIGH : Priority.MEDIUM)
                        .title("Perto do mínimo")
                        .description(ss.getProduct().getProductName()
                                + " → " + ss.getAvailableQuantity() + " restantes")
                        .entityId(String.valueOf(ss.getProduct().getId()))
                        .entityName(ss.getProduct().getProductName())
                        .actionLabel("Criar ordem de compra")
                        .actionEndpoint("/purchase-orders/new?productId=" + ss.getProduct().getId())
                        .detectedAt(now)
                        .colorHex(isCritical ? "#EF9F27" : "#BA7517")
                        .build());
            }
        }
        return alerts;
    }

    private List<AlertItem> buildOrderAlerts(DashboardFilterRequest f) {
        List<AlertItem> alerts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Pedidos internos travados — "Complicações com Pedidos"
        for (Orders o : orderRepo.fetchStuckPendingOrders(now.minusDays(3), f.getDepartmentId())) {
            alerts.add(AlertItem.builder()
                    .id("ORDER_STUCK_" + o.getId())
                    .type(AlertType.ORDER_STUCK)
                    .priority(Priority.HIGH)
                    .title("Pedido PED-" + o.getId())
                    .description("PENDENTE HÁ MAIS DE 3 DIAS")
                    .entityId(String.valueOf(o.getId()))
                    .entityName("PED-" + o.getId())
                    .actionLabel("Ver pedido")
                    .actionEndpoint("/orders/" + o.getId())
                    .detectedAt(now)
                    .colorHex("#EF9F27")
                    .build());
        }

        // Purchase Orders canceladas — "NEGADO" no painel Atenção
        for (PurchaseOrder po : purchaseRepo.fetchOverduePurchaseOrders(now, f.getSupplierId())) {
            alerts.add(AlertItem.builder()
                    .id("PO_OVERDUE_" + po.getId())
                    .type(AlertType.PURCHASE_OVERDUE)
                    .priority(Priority.CRITICAL)
                    .title("Pedido PED-" + po.getId())
                    .description("ENTREGA ATRASADA — " + po.getSupplier().getSupplierName())
                    .entityId(String.valueOf(po.getId()))
                    .entityName("PED-" + po.getId())
                    .actionLabel("Ver ordem")
                    .actionEndpoint("/purchase-orders/" + po.getId())
                    .detectedAt(now)
                    .colorHex("#E24B4A")
                    .build());
        }

        // Tickets parados
        for (Ticket t : ticketRepo.fetchStuckPendingTickets(now.minusDays(2), f.getDepartmentId())) {
            String desc = t.getDescription() != null
                    ? t.getDescription().substring(0, Math.min(60, t.getDescription().length())) + "..."
                    : "Sem descrição";
            alerts.add(AlertItem.builder()
                    .id("TICKET_STUCK_" + t.getId())
                    .type(AlertType.ORDER_STUCK)
                    .priority(Priority.MEDIUM)
                    .title("Ticket #" + t.getId())
                    .description(desc)
                    .entityId(String.valueOf(t.getId()))
                    .entityName("Ticket #" + t.getId())
                    .actionLabel("Ver ticket")
                    .actionEndpoint("/tickets/" + t.getId())
                    .detectedAt(now)
                    .colorHex("#BA7517")
                    .build());
        }

        return alerts;
    }

    private List<AlertItem> buildOperationalAlerts(DashboardFilterRequest f) {
        List<AlertItem> alerts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        long todayOrders = orderRepo.countByStatusAndPeriod("pendente",
                now.toLocalDate().atStartOfDay(), now);
        long weekOrders  = orderRepo.countByStatusAndPeriod("pendente",
                now.minusDays(7), now.toLocalDate().atStartOfDay());
        double dailyAvg  = weekOrders / 7.0;

        if (dailyAvg > 0 && todayOrders > dailyAvg * 2.0) {
            alerts.add(AlertItem.builder()
                    .id("DEMAND_SPIKE_TODAY")
                    .type(AlertType.DEMAND_SPIKE)
                    .priority(Priority.HIGH)
                    .title("Pico de demanda detectado")
                    .description("Volume de hoje (" + todayOrders + ") é o dobro da média diária ("
                            + Math.round(dailyAvg) + ")")
                    .actionLabel("Ver pedidos")
                    .actionEndpoint("/orders?status=pendente")
                    .detectedAt(now)
                    .colorHex("#EF9F27")
                    .build());
        }
        return alerts;
    }

    // ════════════════════════════════════════════════════════════════════
    // Conversores
    // ════════════════════════════════════════════════════════════════════

    private List<MonthlyPoint> toPoints(List<MonthlyCountProjection> raw) {
        return raw.stream().map(p -> MonthlyPoint.builder()
                .year(p.getYear())
                .month(p.getMonth())
                .monthLabel(MonthlyMovementResponse.monthLabel(p.getMonth()))
                .count(p.getCount())
                .totalValue(safeBD(p.getTotalValue()))
                .build()).toList();
    }

    private List<StatusSlice> toSlices(List<StatusCountProjection> raw,
                                       Map<String, String> colors,
                                       Map<String, String> labels) {
        long total = raw.stream().mapToLong(StatusCountProjection::getCount).sum();
        return raw.stream().map(p -> {
            double pct = total == 0 ? 0 : (p.getCount() * 100.0) / total;
            return StatusSlice.builder()
                    .status(p.getStatus())
                    .statusLabel(labels.getOrDefault(p.getStatus(), p.getStatus()))
                    .count(p.getCount())
                    .percentage(round2(pct))
                    .colorHex(colors.getOrDefault(p.getStatus(), "#888780"))
                    .build();
        }).toList();
    }

    private List<SupplierRankItem> toRankItems(List<SupplierRankProjection> raw) {
        return raw.stream().map(p -> {
            long total     = safeL(p.getTotalOrders());
            long cancelled = safeL(p.getCancelledOrders());
            return SupplierRankItem.builder()
                    .supplierId(p.getSupplierId())
                    .supplierName(p.getSupplierName())
                    .isActive(p.getIsActive())
                    .totalOrders(total)
                    .totalValue(safeBD(p.getTotalValue()))
                    .cancelledOrders(cancelled)
                    .cancelRatePct(round2(calcRate(cancelled, total)))
                    .build();
        }).toList();
    }

    private List<CriticalItem> toCriticalItems(List<CriticalStockProjection> raw) {
        return raw.stream().map(p -> {
            String severity = "LOW";
            if (p.getAvailableQuantity() != null && p.getAvailableQuantity() == 0) {
                severity = "OUT_OF_STOCK";
            } else if (p.getMinStockQuantity() != null && p.getAvailableQuantity() != null
                    && p.getAvailableQuantity() < p.getMinStockQuantity() / 2) {
                severity = "CRITICAL";
            }
            return CriticalItem.builder()
                    .productId(p.getProductId())
                    .productName(p.getProductName())
                    .productType(p.getProductType())
                    .supplierName(p.getSupplierName())
                    .stockName(p.getStockName())
                    .availableQuantity(p.getAvailableQuantity())
                    .minStockQuantity(p.getMinStockQuantity())
                    .maxStockQuantity(p.getMaxStockQuantity())
                    .severity(severity)
                    .build();
        }).toList();
    }

    // Status em português + cor + label para a tabela do frontend
    private RecentOrderResponse toRecentOrderResponse(RecentOrderProjection p) {
        String statusKey   = p.getStatus();
        String statusLabel = ORDER_STATUS_LABEL.getOrDefault(statusKey, statusKey);
        return RecentOrderResponse.builder()
                .id(p.getId())
                .type("ORDER")
                .requesterName(p.getUserName())
                .departmentName(p.getDepartmentName())
                .totalValue(p.getTotalValue())
                .status(statusKey)
                .statusLabel(statusLabel)
                .statusColor(ORDER_COLORS.getOrDefault(statusKey, "#888780"))
                .createdAt(p.getCreatedAt())
                .orderDate(p.getOrderDate())
                .criticalityLabel("NORMAL")
                .build();
    }

    private RecentOrderResponse toRecentPurchaseResponse(RecentPurchaseOrderProjection p) {
        String statusKey   = p.getStatus();
        String statusLabel = PURCHASE_STATUS_LABEL.getOrDefault(statusKey, statusKey);

        boolean overdue = p.getExpectedDeliveryDate() != null
                && p.getExpectedDeliveryDate().isBefore(LocalDateTime.now())
                && !"received".equals(statusKey)
                && !"cancelled".equals(statusKey);

        // "Em atraso" sobrescreve o label quando a entrega está vencida
        if (overdue) {
            statusLabel = "Em atraso";
        }

        return RecentOrderResponse.builder()
                .id(p.getId())
                .type("PURCHASE_ORDER")
                .requesterName(p.getUserName())
                .supplierName(p.getSupplierName())
                .totalValue(p.getTotalValue())
                .status(statusKey)
                .statusLabel(statusLabel)
                .statusColor(overdue ? "#E24B4A" : PURCHASE_COLORS.getOrDefault(statusKey, "#888780"))
                .createdAt(p.getCreatedAt())
                .orderDate(p.getOrderDate())
                .expectedDeliveryDate(p.getExpectedDeliveryDate())
                .criticalityLabel(overdue ? "CRITICAL" : "NORMAL")
                .build();
    }

    // ════════════════════════════════════════════════════════════════════
    // Utilitários
    // ════════════════════════════════════════════════════════════════════

    private KpiResponse kpi(String key, String label, Number curr, Number prev) {
        return KpiResponse.of(key, label, curr, prev);
    }

    private KpiResponse kpiWithStatus(String key, String label, Number curr, Number prev, String status) {
        return KpiResponse.of(key, label, curr, prev).toBuilder().status(status).build();
    }

    private long       safeL(Long v)        { return v != null ? v : 0L; }
    private BigDecimal safeBD(BigDecimal v)  { return v != null ? v : BigDecimal.ZERO; }
    private double     round2(double v)      { return Math.round(v * 100.0) / 100.0; }
    private double     calcRate(long part, long total) {
        return total == 0 ? 0.0 : (part * 100.0) / total;
    }

    private static final Map<String, String> ORDER_COLORS = Map.of(
            "pendente",    "#BA7517",
            "processando", "#378ADD",
            "concluido",   "#639922",
            "cancelado",   "#E24B4A"
    );

    private static final Map<String, String> PURCHASE_COLORS = Map.of(
            "draft",     "#888780",
            "pending",   "#BA7517",
            "confirmed", "#378ADD",
            "shipped",   "#7F77DD",
            "received",  "#639922",
            "cancelled", "#E24B4A"
    );
}