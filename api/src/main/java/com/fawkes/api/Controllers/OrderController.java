package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.Request.OrderRequest;
import com.fawkes.api.DTOs.Request.OrderStatusRequest;
import com.fawkes.api.DTOs.Response.OrderResponse;
import com.fawkes.api.Entities.TicketEnum;
import com.fawkes.api.Services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de Pedidos (Tickets internos).
 *
 * Base path: /api/orders
 *
 * Endpoints disponíveis:
 *   GET    /api/orders                        → lista todos os pedidos
 *   GET    /api/orders/{id}                   → busca pedido por ID
 *   GET    /api/orders/user/{userId}           → pedidos de um usuário
 *   GET    /api/orders/department/{deptId}     → pedidos de um departamento
 *   GET    /api/orders/status/{status}         → pedidos por status
 *   POST   /api/orders/user/{userId}           → cria novo pedido
 *   PATCH  /api/orders/{id}/status            → atualiza status
 *   POST   /api/orders/{id}/process           → avança para "processando"
 *   POST   /api/orders/{id}/conclude          → avança para "concluido"
 *   POST   /api/orders/{id}/cancel            → cancela o pedido
 *   PATCH  /api/orders/{id}/note              → vincula nota fiscal
 *   DELETE /api/orders/{id}                   → exclui pedido (apenas pendente)
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // -------------------------------------------------------------------------
    // LEITURAS
    // -------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listAll() {
        return ResponseEntity.ok(orderService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.listByUser(userId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<OrderResponse>> listByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(orderService.listByDepartment(departmentId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> listByStatus(@PathVariable TicketEnum status) {
        return ResponseEntity.ok(orderService.listByStatus(status));
    }

    // -------------------------------------------------------------------------
    // CRIAÇÃO
    // -------------------------------------------------------------------------

    /**
     * Cria um novo pedido vinculado ao usuário informado no path.
     * Body: { "departmentId": 1, "value": 150.00, "orderNoteId": null }
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse created = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // -------------------------------------------------------------------------
    // TRANSIÇÕES DE STATUS
    // -------------------------------------------------------------------------

    /**
     * Atualização genérica de status via body.
     * Body: { "status": "processando" }
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

    /** Atalho: avança o pedido para "processando" */
    @PostMapping("/{id}/process")
    public ResponseEntity<OrderResponse> processOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.processOrder(id));
    }

    /** Atalho: avança o pedido para "concluido" */
    @PostMapping("/{id}/conclude")
    public ResponseEntity<OrderResponse> concludeOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.concludeOrder(id));
    }

    /** Cancela o pedido */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    // -------------------------------------------------------------------------
    // NOTA FISCAL
    // -------------------------------------------------------------------------

    /**
     * Vincula uma nota fiscal ao pedido.
     * Body: { "orderNoteId": 5 }
     */
    @PatchMapping("/{id}/note")
    public ResponseEntity<OrderResponse> attachOrderNote(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        Long orderNoteId = body.get("orderNoteId");
        if (orderNoteId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderService.attachOrderNote(id, orderNoteId));
    }

    // -------------------------------------------------------------------------
    // EXCLUSÃO
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}