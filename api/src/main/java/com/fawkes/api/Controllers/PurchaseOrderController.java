package com.fawkes.api.Controllers;

import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
import com.fawkes.api.Exceptions.RegraDeNegocioException;
import com.fawkes.api.Services.PurchaseOrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fawkes.api.DTOs.Request.ConfirmOrderRequest;
import com.fawkes.api.DTOs.Request.ReceiveOrderRequest;
import com.fawkes.api.DTOs.Request.UpdateOrderRequest;
import com.fawkes.api.DTOs.Request.UpdateItemPricesRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fawkes.api.Services.PurchaseOrderEmail;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {


    @Autowired
    private PurchaseOrderEmail purchaseOrderEmail;

    @Value("${app.email.order.recipient}")
    private String orderRecipientEmail;

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
        return purchaseOrderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrder> update(
            @PathVariable Long id,
            @RequestBody UpdateOrderRequest request) {
        return ResponseEntity.ok(purchaseOrderService.updateOrder(id, request));
    }

    @PutMapping("/{id}/items/prices")
    public ResponseEntity<PurchaseOrder> fillItemPrices(
            @PathVariable Long id,
            @RequestBody UpdateItemPricesRequest request) {
        return ResponseEntity.ok(purchaseOrderService.fillItemPrices(id, request));
    }

    @PostMapping("/draft")
    public ResponseEntity<PurchaseOrder> createDraft(@RequestBody Map<String, Long> request) {
        Long supplierId = request.get("supplierId");
        Long userId = request.get("userId");
        if (supplierId == null || userId == null) {
            throw new RegraDeNegocioException("supplierId e userId são obrigatórios");
        }
        return ResponseEntity.ok(purchaseOrderService.createDraft(supplierId, userId));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<PurchaseOrder> addItem(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> request) {
        Object productIdObj = request.get("productId");
        Object quantityObj = request.get("quantity");
        Object unitPriceObj = request.get("unitPrice");
        if (productIdObj == null || quantityObj == null || unitPriceObj == null) {
            throw new RegraDeNegocioException("productId, quantity e unitPrice são obrigatórios");
        }
        return ResponseEntity.ok(purchaseOrderService.addItem(
                orderId,
                ((Number) productIdObj).longValue(),
                ((Number) quantityObj).intValue(),
                new BigDecimal(unitPriceObj.toString())
        ));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PurchaseOrder> submit(@PathVariable Long id) {
        PurchaseOrder order = purchaseOrderService.submitOrder(id);
        purchaseOrderEmail.sendEmail(id, orderRecipientEmail);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<PurchaseOrder> confirm(
            @PathVariable Long id,
            @RequestBody(required = false) ConfirmOrderRequest request) {
        return ResponseEntity.ok(purchaseOrderService.confirmOrder(id, request));
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

    @PostMapping("/{id}/problem")
    public ResponseEntity<PurchaseOrder> markAsProblem(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(purchaseOrderService.markAsProblem(id, reason));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<PurchaseOrder> markAsReturned(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.markAsReturned(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}