package com.fawkes.api.DTOs.Response;

import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Entities.PurchaseOrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseOrderResponse {

    private Long id;
    private String status;
    private BigDecimal totalValue;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;
    private String notes;

    // Dados do criador (para coluna "Solicitante" e "Setor" na tabela)
    private CreatedByInfo createdBy;

    // Dados do fornecedor (para coluna "Pagamento")
    private SupplierInfo supplier;

    // Itens (para coluna "Produto" e "Qtd")
    private List<ItemInfo> items;

    // Fábrica estática
    public static PurchaseOrderResponse from(PurchaseOrder order) {
        PurchaseOrderResponse dto = new PurchaseOrderResponse();
        dto.id         = order.getId();
        dto.status     = order.getStatus() != null ? order.getStatus().name() : "UNKNOWN";
        dto.totalValue = order.getTotalValue();
        dto.orderDate  = order.getOrderDate();
        dto.createdAt  = order.getCreatedAt();
        dto.notes      = order.getNotes();

        // Solicitante + Setor
        if (order.getCreatedBy() != null) {
            CreatedByInfo cb = new CreatedByInfo();
            cb.id       = order.getCreatedBy().getId();
            cb.userName = order.getCreatedBy().getUserName();

            if (order.getCreatedBy().getDepartments() != null) {
                DepartmentInfo dept = new DepartmentInfo();
                dept.id              = order.getCreatedBy().getDepartments().getId();
                dept.departamentName = order.getCreatedBy().getDepartments().getDepartamentName();
                cb.departments = dept;
            }
            dto.createdBy = cb;
        }

        // Fornecedor + métodos de pagamento
        if (order.getSupplier() != null) {
            SupplierInfo sup = new SupplierInfo();
            sup.id           = order.getSupplier().getId();
            sup.supplierName = order.getSupplier().getSupplierName();
            // paymentMethods é um Set<PaymentMethod> — converte para List<String>
            if (order.getSupplier().getPaymentMethods() != null) {
                sup.paymentMethods = order.getSupplier().getPaymentMethods()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toList());
            }
            dto.supplier = sup;
        }

        // Itens do pedido
        if (order.getItems() != null) {
            dto.items = order.getItems().stream().map(item -> {
                ItemInfo info = new ItemInfo();
                info.id       = item.getId();
                info.quantity = item.getQuantity();
                info.unitPrice = item.getUnitPrice();
                info.totalPrice = item.getTotalPrice();
                if (item.getProduct() != null) {
                    ProductInfo p = new ProductInfo();
                    p.id          = item.getProduct().getId();
                    p.productName = item.getProduct().getProductName();
                    info.product  = p;
                }
                return info;
            }).collect(Collectors.toList());
        } else {
            dto.items = Collections.emptyList();
        }

        return dto;
    }

    // Getters
    public Long getId()                  { return id; }
    public String getStatus()            { return status; }
    public BigDecimal getTotalValue()    { return totalValue; }
    public LocalDateTime getOrderDate()  { return orderDate; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public String getNotes()             { return notes; }
    public CreatedByInfo getCreatedBy()  { return createdBy; }
    public SupplierInfo getSupplier()    { return supplier; }
    public List<ItemInfo> getItems()     { return items; }

    // Inner DTOs

    public static class CreatedByInfo {
        public Long id;
        public String userName;
        public DepartmentInfo departments;
    }

    public static class DepartmentInfo {
        public Long id;
        public String departamentName;
    }

    public static class SupplierInfo {
        public Long id;
        public String supplierName;
        public List<String> paymentMethods;
    }

    public static class ItemInfo {
        public Long id;
        public Integer quantity;
        public BigDecimal unitPrice;
        public BigDecimal totalPrice;
        public ProductInfo product;
    }

    public static class ProductInfo {
        public Long id;
        public String productName;
    }
}