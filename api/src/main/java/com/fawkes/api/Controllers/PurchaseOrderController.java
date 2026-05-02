package com.fawkes.api.Controllers;


import com.fawkes.api.DTOs.Response.PurchaseOrderResponse;
import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Services.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    // Leituras (retornam DTO para evitar serialização LAZY)

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> listAll() {
        return ResponseEntity.ok(
                purchaseOrderService.listAll().stream()
                        .map(PurchaseOrderResponse::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderResponse>> listBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(
                purchaseOrderService.listBySupplier(supplierId).stream()
                        .map(PurchaseOrderResponse::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseOrderResponse>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                purchaseOrderService.listByUser(userId).stream()
                        .map(PurchaseOrderResponse::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrderResponse>> listByStatus(@PathVariable PurchaseOrder.Status status) {
        return ResponseEntity.ok(
                purchaseOrderService.listByStatus(status).stream()
                        .map(PurchaseOrderResponse::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse> getById(@PathVariable Long id) {
        return purchaseOrderService.getById(id)
                .map(PurchaseOrderResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Escritas

    @PostMapping("/draft")
    public ResponseEntity<PurchaseOrderResponse> createDraft(@RequestBody Map<String, Long> request) {
        PurchaseOrder order = purchaseOrderService.createDraft(
                request.get("supplierId"),
                request.get("userId")
        );
        return ResponseEntity.ok(PurchaseOrderResponse.from(order));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<PurchaseOrderResponse> addItem(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> request) {
        PurchaseOrder order = purchaseOrderService.addItem(
                orderId,
                ((Number) request.get("productId")).longValue(),
                ((Number) request.get("quantity")).intValue(),
                new BigDecimal(request.get("unitPrice").toString())
        );
        return ResponseEntity.ok(PurchaseOrderResponse.from(order));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PurchaseOrderResponse> submit(@PathVariable Long id) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.submitOrder(id)));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<PurchaseOrderResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.confirmOrder(id)));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<PurchaseOrderResponse> markAsShipped(@PathVariable Long id) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.markAsShipped(id)));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrderResponse> receive(@PathVariable Long id) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.receiveOrder(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.cancelOrder(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}