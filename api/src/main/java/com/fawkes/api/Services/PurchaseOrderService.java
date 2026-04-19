package com.fawkes.api.Services;

import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Entities.PurchaseOrderItem;
import com.fawkes.api.Entities.Suppliers;
import com.fawkes.api.Entities.Users;
import com.fawkes.api.Repositories.PurchaseOrderRepository;
import com.fawkes.api.Repositories.SupplierRepository;
import com.fawkes.api.Repositories.UserRepository;
import com.fawkes.api.Repositories.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductsRepository productsRepository;

    public List<PurchaseOrder> listAll() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrder> listBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId);
    }

    public List<PurchaseOrder> listByUser(Long userId) {
        return purchaseOrderRepository.findByCreatedById(userId);
    }

    public List<PurchaseOrder> listByStatus(PurchaseOrder.Status status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    public Optional<PurchaseOrder> getById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Transactional
    public PurchaseOrder createDraft(Long supplierId, Long userId) {
        Suppliers supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setCreatedBy(user);
        order.setStatus(PurchaseOrder.Status.draft);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder addItem(Long orderId, Long productId, Integer quantity, BigDecimal unitPrice) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != PurchaseOrder.Status.draft && order.getStatus() != PurchaseOrder.Status.pending) {
            throw new IllegalArgumentException("Cannot add items to order with status: " + order.getStatus());
        }

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setPurchaseOrder(order);
        item.setProduct(productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found")));
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));

        order.getItems().add(item);
        recalculateTotal(order);

        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder submitOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot submit empty order");
        }

        order.setStatus(PurchaseOrder.Status.pending);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder confirmOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(PurchaseOrder.Status.confirmed);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder markAsShipped(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(PurchaseOrder.Status.shipped);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder receiveOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(PurchaseOrder.Status.received);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder cancelOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == PurchaseOrder.Status.received || order.getStatus() == PurchaseOrder.Status.cancelled) {
            throw new IllegalArgumentException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(PurchaseOrder.Status.cancelled);
        return purchaseOrderRepository.save(order);
    }

    private void recalculateTotal(PurchaseOrder order) {
        BigDecimal total = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (PurchaseOrderItem item : order.getItems()) {
                total = total.add(item.getTotalPrice());
            }
        }
        order.setTotalValue(total);
    }

    public void delete(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != PurchaseOrder.Status.draft) {
            throw new IllegalArgumentException("Only draft orders can be deleted");
        }

        purchaseOrderRepository.deleteById(id);
    }
}