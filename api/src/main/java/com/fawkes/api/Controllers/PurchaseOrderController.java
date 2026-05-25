package com.fawkes.api.Controllers;

import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Services.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fawkes.api.DTOs.Request.ReceiveOrderRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> listAll() {
        return ResponseEntity.ok(purchaseOrderService.listAll());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>> listBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(purchaseOrderService.listBySupplier(supplierId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseOrder>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(purchaseOrderService.listByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> listByStatus(@PathVariable PurchaseOrder.Status status) {
        return ResponseEntity.ok(purchaseOrderService.listByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getById(id).orElse(null));
    }

    @PostMapping("/draft")
    public ResponseEntity<PurchaseOrder> createDraft(@RequestBody Map<String, Long> request) {
        return ResponseEntity.ok(purchaseOrderService.createDraft(
                request.get("supplierId"),
                request.get("userId")
        ));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<PurchaseOrder> addItem(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(purchaseOrderService.addItem(
                orderId,
                ((Number) request.get("productId")).longValue(),
                ((Number) request.get("quantity")).intValue(),
                new BigDecimal(request.get("unitPrice").toString())
        ));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PurchaseOrder> submit(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.submitOrder(id));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<PurchaseOrder> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.confirmOrder(id));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<PurchaseOrder> markAsShipped(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.markAsShipped(id));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrder> receive(
            @PathVariable Long id,
            @RequestBody ReceiveOrderRequest request) {
        return ResponseEntity.ok(purchaseOrderService.receiveOrder(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrder> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.cancelOrder(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}